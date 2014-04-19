package de.kahoona.xtv.irc

import de.kahoona.xtv.Download
import lombok.Cleanup
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.dcc.ReceiveFileTransfer
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.Message

/**
 * Created by Benni on 08.04.14.
 */
class XTVReceiveFileTransfer extends ReceiveFileTransfer{

  Vertx    vertx
  Download download

  long lastTimeStamp = 0


  XTVReceiveFileTransfer(Configuration<PircBotX> configuration, Socket socket, User user, File file, long startPosition) {
    super(configuration, socket, user, file, startPosition)
  }

  @Override
  protected void onAfterSend () {
    download.status = 'RUNNING'
    long now = System.currentTimeMillis()
    download.speed = calcCurrentSpeed (download.bytesReceived ?: 0, this.bytesTransfered, lastTimeStamp, now)
    download.bytesReceived = this.bytesTransfered

    vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download.toMap()])
    lastTimeStamp = now
  }

  static double calcCurrentSpeed(long oldSize, long newSize, long oldTime, long newTime) {
    double sizeDelta = (newSize - oldSize) / 1024
    double timeDelta = (newTime - oldTime) / 1000
    return sizeDelta / timeDelta
  }

  protected stopDownload () {
    download.status = 'FAILED'
    download.bytesReceived = this.bytesTransfered
    vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download.toMap()])
  }

  protected completeDownload () {
    download.status = 'COMPLETE'
    download.bytesReceived = this.bytesTransfered
    vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download.toMap()]) {
      vertx.eventBus.send ('xtv.downloadComplete',[data: download.toMap ()])
    }
  }

  @Override
  protected void transferFile() throws IOException {
    @Cleanup
    BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
    @Cleanup
    OutputStream socketOutput = socket.getOutputStream();
    @Cleanup
    RandomAccessFile fileOutput = new RandomAccessFile(file.getCanonicalPath(), "rw");
    fileOutput.seek(startPosition);

    bytesTransfered = startPosition

    //Recieve file
    byte[] inBuffer = new byte[configuration.getDccTransferBufferSize()];
    byte[] outBuffer = new byte[4];
    int bytesRead = 0;
    int counter = 0

    try {
      while ((bytesRead = socketInput.read(inBuffer, 0, inBuffer.length)) != -1) {
        fileOutput.write(inBuffer, 0, bytesRead);
        bytesTransfered += bytesRead;
        //Send back an acknowledgement of how many bytes we have got so far.
        //Convert bytesTransfered to an "unsigned, 4 byte integer in network byte order", per DCC specification
        outBuffer[0] = (byte) ((bytesTransfered >> 24) & 0xff);
        outBuffer[1] = (byte) ((bytesTransfered >> 16) & 0xff);
        outBuffer[2] = (byte) ((bytesTransfered >> 8) & 0xff);
        outBuffer[3] = (byte) (bytesTransfered & 0xff);
        socketOutput.write(outBuffer);

        counter++

        if (counter == 1000) {
          onAfterSend();
          counter = 0;
        }
      }

      //download is complete
      fileOutput.close ()
      completeDownload()
    } catch (Exception ignored) {
      stopDownload ()
    }
  }
}

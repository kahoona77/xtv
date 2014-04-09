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


  XTVReceiveFileTransfer(Configuration<PircBotX> configuration, Socket socket, User user, File file, long startPosition) {
    super(configuration, socket, user, file, startPosition)
  }

  @Override
  protected void onAfterSend () {
    download.bytesReceived = this.bytesTransfered
    vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download])
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

    //Recieve file
    byte[] inBuffer = new byte[configuration.getDccTransferBufferSize()];
    byte[] outBuffer = new byte[4];
    int bytesRead = 0;
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
      onAfterSend();
    }
  }
}

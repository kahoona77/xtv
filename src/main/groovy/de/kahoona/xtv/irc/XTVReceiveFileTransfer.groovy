package de.kahoona.xtv.irc

import de.kahoona.xtv.data.StreamManager
import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.domain.XtvSettings
import lombok.Cleanup
import org.apache.commons.lang3.StringUtils
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.dcc.ReceiveFileTransfer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files

/**
 * Created by Benni on 08.04.14.
 */
class XTVReceiveFileTransfer extends ReceiveFileTransfer{

  private static Logger log = LoggerFactory.getLogger(XTVReceiveFileTransfer.class)

  XtvSettings   settings
  StreamManager streamManager
  Download      download


  long lastTimeStamp = 0


  XTVReceiveFileTransfer(Configuration<PircBotX> configuration, Socket socket, User user, File file, long startPosition) {
    super(configuration, socket, user, file, startPosition)
  }

  @Override
  protected void onAfterSend () {
    download.status = 'RUNNING'
    long now = System.currentTimeMillis()
    download.speed = calcCurrentSpeed (download.bytesReceived, this.bytesTransfered, lastTimeStamp, now)
    download.bytesReceived = this.bytesTransfered

    lastTimeStamp = now
  }

  static double calcCurrentSpeed(long oldSize, long newSize, long oldTime, long newTime) {
    double sizeDelta = (newSize - oldSize) / 1024
    double timeDelta = (newTime - oldTime) / 1000
    return sizeDelta / timeDelta
  }

  boolean isRunning() {
    download.status != 'FAILED'
  }

  protected stopDownload () {
    download.status = 'FAILED'
    download.bytesReceived = this.bytesTransfered
    log.info("Download stopped: ${download.file}")
  }

  protected completeDownload () {
    log.info("Download complete: ${download.file}")
    download.status = 'COMPLETE'
    download.bytesReceived = this.bytesTransfered

    File file = new File(settings.tempDir, StringUtils.trim (download.file))
    if (file.exists()) {
      try {
        File destination = new File(settings.downloadDir, file.getName())
        Files.move(file.toPath(), destination.toPath())

        // execute post download trigger
        if (settings.postDownloadTrigger) {
          String trigger = "${settings.postDownloadTrigger} $file.absolutePath"
          trigger.execute()
        }

      } catch (IOException e) {
        log.error("Error while moving file '${file.absolutePath}'",e)
      }
    } else {
      log.error  ("Error did not find file '${file.absolutePath}' to move.")
    }
  }

  @Override
  protected void transferFile() throws IOException {
    log.info("Download starting transfer: ${download.file}")
    @Cleanup
    InputStream socketInput = streamManager.registerStream(socket.getInputStream());
    @Cleanup
    OutputStream socketOutput = socket.getOutputStream();
    @Cleanup
    RandomAccessFile fileOutput = new RandomAccessFile(file.getCanonicalPath(), "rw");

    fileOutput.seek(startPosition);
    bytesTransfered = startPosition

    //Recieve file
    byte[] inBuffer = new byte[configuration.getDccTransferBufferSize()];
    byte[] outBuffer = new byte[4];
    int bytesRead;
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

        if (counter == 500) {
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

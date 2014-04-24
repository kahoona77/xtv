package de.kahoona.xtv.irc

import lombok.NonNull
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.Utils
import org.pircbotx.dcc.DccHandler
import org.pircbotx.exception.DccException
import org.pircbotx.hooks.events.IncomingChatRequestEvent
import org.pircbotx.hooks.events.IncomingFileTransferEvent

import java.util.concurrent.CountDownLatch

@SuppressWarnings(["UnnecessaryQualifiedReference"])
class XtvDccHandler extends DccHandler{

  XtvDccHandler(@NonNull PircBotX bot) {
    super(bot)
  }

  @Override
  public boolean processDcc(final User user, String request) throws IOException {
    List<String> requestParts = tokenizeDccRequest(request);
    String type = requestParts.get(1);
    if (type.equals("SEND")) {
      //Someone is trying to send a file to us
      //Example: DCC SEND <filename> <ip> <port> <file size> <transferToken> (note File size is optional)
      String rawFilename = requestParts.get(2);
      final String safeFilename = (rawFilename.startsWith("\"") && rawFilename.endsWith("\"")) ? rawFilename.substring(1, rawFilename.length() - 1) : rawFilename;
      InetAddress address = integerToAddress(requestParts.get(3));
      int port = Integer.parseInt(requestParts.get(4));
      long size = Integer.parseInt(Utils.tryGetIndex(requestParts, 5, "-1"));
      String transferToken = Utils.tryGetIndex(requestParts, 6, null);

      if (transferToken != null)
      //Check if this is an acknowledgement of a passive dcc file request
        synchronized (pendingSendPassiveTransfers) {
          Iterator<Map.Entry<DccHandler.PendingSendFileTransferPassive, CountDownLatch>> pendingItr = pendingSendPassiveTransfers.entrySet().iterator();
          while (pendingItr.hasNext()) {
            Map.Entry<DccHandler.PendingSendFileTransferPassive, CountDownLatch> curEntry = pendingItr.next();
            DccHandler.PendingSendFileTransferPassive transfer = curEntry.getKey();
            if (transfer.getUser() == user && transfer.getFilename().equals(rawFilename)
                && transfer.getTransferToken().equals(transferToken)) {
              transfer.setReceiverAddress(address);
              transfer.setReceiverPort(port);
              curEntry.getValue().countDown();
              pendingItr.remove();
              return true;
            }
          }
        }

      //Nope, this is a new transfer
      if (port == 0 || transferToken != null)
      //User is trying to use reverse DCC
        bot.getConfiguration().getListenerManager().dispatchEvent(new IncomingFileTransferEvent<PircBotX>(bot, user, rawFilename, safeFilename, address, port, size, transferToken, true));
      else
        bot.getConfiguration().getListenerManager().dispatchEvent(new IncomingFileTransferEvent<PircBotX>(bot, user, rawFilename, safeFilename, address, port, size, transferToken, false));
    } else if (type.equals("RESUME")) {
      //Someone is trying to resume sending a file to us
      //Example: DCC RESUME <filename> 0 <position> <token>
      //Reply with: DCC ACCEPT <filename> 0 <position> <token>
      String filename = requestParts.get(2);
      int port = Integer.parseInt(requestParts.get(3));
      long position = Integer.parseInt(requestParts.get(4));

      if (port == 0) {
        //Passive transfer
        String transferToken = requestParts.get(5);
        synchronized (pendingSendPassiveTransfers) {
          Iterator<Map.Entry<DccHandler.PendingSendFileTransferPassive, CountDownLatch>> pendingItr = pendingSendPassiveTransfers.entrySet().iterator();
          while (pendingItr.hasNext()) {
            Map.Entry<DccHandler.PendingSendFileTransferPassive, CountDownLatch> curEntry = pendingItr.next();
            DccHandler.PendingSendFileTransferPassive transfer = curEntry.getKey();
            if (transfer.getUser() == user && transfer.getFilename().equals(filename)
                && transfer.getTransferToken().equals(transferToken)) {
              transfer.setStartPosition(position);
              return true;
            }
          }
        }
      } else
        synchronized (pendingSendTransfers) {
          Iterator<DccHandler.PendingSendFileTransfer> pendingItr = pendingSendTransfers.iterator();
          while (pendingItr.hasNext()) {
            DccHandler.PendingSendFileTransfer transfer = pendingItr.next();
            if (transfer.getUser() == user && transfer.getFilename().equals(filename)
                && transfer.getPort() == port) {
              transfer.setPosition(position);
              return true;
            }
          }
        }

      //Haven't returned yet, received an unknown transfer
      throw new DccException(DccException.Reason.UnknownFileTransferResume, user, "Transfer line: " + request);
    } else if (type.equals("ACCEPT")) {
      //Someone is acknowledging a transfer resume
      //Example: DCC ACCEPT <filename> 0 <position> <token> (if 0 exists then its a passive connection)
      String filename = requestParts.get(2);
      int dataPosition = 4; //(requestParts.size() == 5) ? 3 : 4;
      long position = Integer.parseInt(requestParts.get(dataPosition));
      synchronized (pendingReceiveTransfers) {
        Iterator<Map.Entry<DccHandler.PendingRecieveFileTransfer, CountDownLatch>> pendingItr = pendingReceiveTransfers.entrySet().iterator();
        while (pendingItr.hasNext()) {
          Map.Entry<DccHandler.PendingRecieveFileTransfer, CountDownLatch> curEntry = pendingItr.next();
          IncomingFileTransferEvent<PircBotX> transferEvent = curEntry.getKey().getEvent();
          if (transferEvent.getUser() == user && transferEvent.getRawFilename().equals(filename)) {
            curEntry.getKey().setPosition(position);
            curEntry.getValue().countDown();
            pendingItr.remove();
            return true;
          }
        }
      }
    } else if (type.equals("CHAT")) {
      //Someone is trying to chat with us
      //Example: DCC CHAT <protocol> <ip> <port> (protocol should be chat)
      InetAddress address = integerToAddress(requestParts.get(3));
      int port = Integer.parseInt(requestParts.get(4));
      String chatToken = Utils.tryGetIndex(requestParts, 5, null);

      //Check if this is an acknowledgement of a passive chat request
      if (chatToken != null)
        synchronized (pendingSendPassiveChat) {
          Iterator<Map.Entry<DccHandler.PendingSendChatPassive, CountDownLatch>> pendingItr = pendingSendPassiveChat.entrySet().iterator();
          while (pendingItr.hasNext()) {
            Map.Entry<DccHandler.PendingSendChatPassive, CountDownLatch> curEntry = pendingItr.next();
            DccHandler.PendingSendChatPassive pendingChat = curEntry.getKey();
            if (pendingChat.getUser() == user && pendingChat.getChatToken().equals(chatToken)) {
              pendingChat.setReceiverAddress(address);
              pendingChat.setReceiverPort(port);
              curEntry.getValue().countDown();
              pendingItr.remove();
              return true;
            }
          }
        }

      //Nope, this is a new chat
      if (port == 0 && chatToken != null)
        bot.getConfiguration().getListenerManager().dispatchEvent(new IncomingChatRequestEvent<PircBotX>(bot, user, address, port, chatToken, true));
      else
        bot.getConfiguration().getListenerManager().dispatchEvent(new IncomingChatRequestEvent<PircBotX>(bot, user, address, port, chatToken, false));
    } else
      return false;
    return true;
  }
}

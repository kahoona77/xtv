package de.kahoona.xtv.irc

import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.UtilSSLSocketFactory
import org.pircbotx.cap.TLSCapHandler
import org.pircbotx.dcc.ReceiveChat
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.ChannelInfoEvent
import org.pircbotx.hooks.events.IncomingChatRequestEvent
import org.pircbotx.hooks.events.JoinEvent
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.events.NoticeEvent
import org.pircbotx.hooks.events.UserListEvent
import org.pircbotx.hooks.types.GenericMessageEvent

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcBot extends ListenerAdapter{

  String name
  String server
  List<String> channels

  public IrcBot (name, server, List<String> channels) {
    this.name = name
    this.server = server
    this.channels = channels
  }

  public connect () {
    Configuration.Builder builder = new Configuration.Builder ()
      .setName (this.name) //Set the nick of the bot. CHANGE IN YOUR CODE
      .setAutoNickChange (true) //Automatically change nick when the current one is in use
      .setCapEnabled (true) //Enable CAP features
      .addCapHandler (new TLSCapHandler (new UtilSSLSocketFactory ().trustAllCertificates (), true))
      .addListener (this) //This class is a listener, so add it to the bots known listeners
      .setServerHostname (this.server)

    channels.each {
      builder.addAutoJoinChannel (it)
    }

    Configuration configuration = builder.buildConfiguration ()
    PircBotX bot = new PircBotX (configuration)
    bot.startBot ()
  }


  @Override
  public void onMessage(final MessageEvent event) throws Exception {

    Packet packet = PacketParser.getPacket (event.user.nick, event.message)
    if (packet) {
      println "[got packet] bot: ${packet.bot}; id:${packet.id}; name: ${packet.name}; size:${packet.size}"
    }

  }


  @Override
  public void onNotice(NoticeEvent event) throws Exception {
    println ("irc: $event.message")
  }

//  @Override
//  public void onUserList(UserListEvent event) throws Exception {
//    println ("onUserList: $event")
//    event.users.each {User user ->
//      println "user: $user"
//    }
//  }



}

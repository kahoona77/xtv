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
import org.vertx.groovy.core.Vertx

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcBot extends ListenerAdapter{

  String name
  String server
  List<String> channels

  private Vertx    vertx
  private PircBotX bot

  public IrcBot (Vertx vertx, name, server, List<String> channels) {
    this.vertx = vertx
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
    this.bot = new PircBotX (configuration)
    bot.startBot ()
  }


  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    Packet packet = PacketParser.getPacket (event.channel.name, event.user.nick, event.message)
    if (packet) {
      vertx.eventBus.send ('xtv.savePacket', [data: packet])
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

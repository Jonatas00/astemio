package events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Logger extends ListenerAdapter {

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getMessage().getAuthor().isBot()) return;

    System.out.printf("[%s] - [%s] - [%s]: %s\n", event.getMessage().getGuild().getName(), event.getMessage().getChannelId(), event.getAuthor().getName(), event.getMessage().getContentRaw());
  }
}

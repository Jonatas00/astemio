package Event;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceiveListener extends ListenerAdapter {
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) return;

    String mensagemRecebida = event.getMessage().getContentRaw();

    event.getChannel().sendMessage("O "+ event.getAuthor().getName() + " disse " + mensagemRecebida).queue();
  }
}
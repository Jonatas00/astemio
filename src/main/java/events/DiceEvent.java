package events;

import commands.dice.Dice;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceEvent extends ListenerAdapter {
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) return;

    String message = event.getMessage().getContentRaw();

    Pattern dice = Pattern.compile("d\\d+");
    Matcher matcher = dice.matcher(message);

    if (matcher.find()) {
      if (message.matches("^\\d*#?\\s*(\\d+d\\d+|\\d+)(\\s*[+\\-*/]\\s*(\\d+d\\d+|\\d+))*\\s*(\\s+[\\p{L}\\p{M}0-9\\- ]+)?$")) {
        Dice.diceCommand(event);
      }
    }
  }
}

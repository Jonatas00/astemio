import Event.MessageReceiveListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {
  public static void main(String[] args) throws Exception {
    String token = System.getenv("BOT_TOKEN");

    JDABuilder bot = JDABuilder.createDefault(token);
    bot.enableIntents(EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT));
    bot.addEventListeners(new MessageReceiveListener());

    bot.build();
  }
}

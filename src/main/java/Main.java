import Event.EventListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {
  public static void main(String[] args) {
    String token = System.getenv("BOT_TOKEN");

    JDABuilder bot = JDABuilder.createDefault(token);
    bot.enableIntents(EnumSet.allOf(GatewayIntent.class));
    bot.addEventListeners(new EventListener());

    System.out.println("test");
    bot.build();
  }
}

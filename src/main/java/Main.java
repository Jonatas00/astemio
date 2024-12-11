import event.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("BOT_TOKEN");

    JDABuilder bot = JDABuilder.createDefault(token);
    bot.enableIntents(EnumSet.allOf(GatewayIntent.class));
    bot.addEventListeners(new EventListener());

    bot.build();
  }
}

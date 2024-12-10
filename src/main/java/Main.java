import Event.MessageReceiveListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {
  public static void main(String[] args) {
    String token = System.getenv("BOT_TOKEN");

    JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
            .addEventListeners(new MessageReceiveListener())
            .build();
  }
}

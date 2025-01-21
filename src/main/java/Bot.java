import config.Token;
import events.DiceEvent;
import events.Logger;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.EnumSet;

public class Bot {

  public Bot() {
    DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.customStatus("Online e operante"));
    builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
    ShardManager shardManager = builder.build();

    shardManager.addEventListener(new Logger(), new DiceEvent());
  }
}

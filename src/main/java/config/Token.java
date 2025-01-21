package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Token {
  public static String BOT_TOKEN = Dotenv.load().get("BOT_TOKEN");
}

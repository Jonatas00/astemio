package Event;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EventListener extends ListenerAdapter {
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (!event.getMessage().getChannelId().equals("1316473764485009510")) return;
    if (event.getAuthor().isBot()) return;

    String message = event.getMessage().getContentRaw().trim();

    if (message.matches("\\d+d\\d+")) {
      Random random = new Random();
      StringBuilder result = new StringBuilder();

      try {
        String[] parts = message.split("d");
        BigInteger quantity = new BigInteger(parts[0]);
        BigInteger sides = new BigInteger(parts[1]);

        if (quantity.compareTo(BigInteger.ZERO) <= 0 || sides.compareTo(BigInteger.ZERO) <= 0) {
          event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Por favor, insira valores positivos para os dados.").queue();
          return;
        }

        List<BigInteger> rolls = new ArrayList<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(quantity) < 0; i = i.add(BigInteger.ONE)) {
          rolls.add(randomBigInteger(sides, random).add(BigInteger.ONE));
        }

        BigInteger total = rolls.stream().reduce(BigInteger.ZERO, BigInteger::add);
        rolls.sort(Collections.reverseOrder()); // Ordena em ordem decrescente

        result.append("` ").append(total).append(" `").append(" ⟵ [ ");
        for (int i = 0; i < rolls.size(); i++) {
          result.append(rolls.get(i)).append(i < rolls.size() - 1 ? ", " : "");
        }
        result.append("] ").append(message);

        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + result).queue();

      } catch (NumberFormatException e) {
        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Erro ao interpretar os números. Use o formato 'XdY', como '2d6'.").queue();
      }
    }
  }

  /**
   * Generates a random BigInteger value less than the given upper bound.
   *
   * @param upperBound the exclusive upper bound for the random value
   * @param random     the Random instance to use
   * @return a random BigInteger less than upperBound
   */
  private BigInteger randomBigInteger(BigInteger upperBound, Random random) {
    BigInteger result;
    do {
      result = new BigInteger(upperBound.bitLength(), random);
    } while (result.compareTo(upperBound) >= 0);
    return result;
  }
}

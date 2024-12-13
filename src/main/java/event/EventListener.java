package event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventListener extends ListenerAdapter {
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    String message = event.getMessage().getContentRaw().trim();

    if (message.matches("^\\d*#?(\\d+d\\d+|\\d+)(\\s*[+\\-*/]\\s*(\\d+d\\d+|\\d+))*$")) {
      try {
        String[] parts = message.split("#", 2);
        int rows = 1;

        if (parts.length > 1) {
          rows = Integer.parseInt(parts[0]);
          message = parts[1];
        }

        List<String> expressions = diceParser(message);

        for (int i = 1; i < expressions.size(); i++) {
          if ("+-*/".contains(expressions.get(i)) && "+-*/".contains(expressions.get(i - 1))) {
            event.getChannel().sendMessage("Formato inválido: operadores consecutivos não são permitidos.").queue();
            return;
          }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.orange);

        List<String> results = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
          Integer total = null;
          StringBuilder computation = new StringBuilder();

          for (int i = 0; i < expressions.size(); i++) {
            String expr = expressions.get(i).trim();
            if (expr.matches("\\d+d\\d+")) {
              String[] diceParts = expr.split("d");
              int quantity = Integer.parseInt(diceParts[0]);
              int sides = Integer.parseInt(diceParts[1]);
              List<Integer> rolls = IntStream.range(0, quantity)
                  .mapToObj(j -> new Random().nextInt(sides) + 1)
                  .collect(Collectors.toList());
              int sum = rolls.stream().mapToInt(Integer::intValue).sum();
              computation.append(rolls).append(" ");
              total = (total == null) ? sum : applyOperator(expressions.get(i - 1), total, sum);
            } else if (expr.matches("\\d+")) {
              int value = Integer.parseInt(expr);
              computation.append(value).append(" ");
              total = (total == null) ? value : applyOperator(expressions.get(i - 1), total, value);
            } else if (!"+-*/".contains(expr)) {
              event.getChannel().sendMessage("Operador inválido: " + expr).queue();
              return;
            }
          }

          if (total != null) {
            results.add("` " + total + " `" + " ⟵ [" + computation.toString().trim() + "]");
          } else {
            event.getChannel().sendMessage("Expressão inválida. Verifique sua entrada.").queue();
            return;
          }
        }

        embed.addField("Jogador:", event.getAuthor().getAsMention(), true);
        embed.addField("Resultados:", String.join("\n", results), false);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
      } catch (NumberFormatException e) {
        event.getChannel().sendMessage("Erro ao interpretar os números. Use um formato válido como '1d10+2', '2#1d10+3', etc.").queue();
      }
    }
  }

  private int applyOperator(String operator, int left, int right) {
    return switch (operator) {
      case "+" -> left + right;
      case "-" -> left - right;
      case "*" -> left * right;
      case "/" -> right != 0 ? left / right : left; // Evita divisão por zero
      default -> throw new IllegalArgumentException("Operador inválido: " + operator);
    };
  }

  private List<String> diceParser(String str) {
    List<String> parameters = new ArrayList<>();
    Pattern pat = Pattern.compile("[+\\-*/]|\\d+d\\d+|\\d+");
    Matcher mat = pat.matcher(str);

    while (mat.find()) {
      parameters.add(mat.group());
    }

    return parameters;
  }
}

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
    if (!event.getChannel().getId().equals("1012026375735083088")) return;

    String rawMessage = event.getMessage().getContentRaw().trim();

    // Validação do formato do comando
    if (rawMessage.matches("^\\d*#?(\\d+d\\d+|\\d+)(\\s*[+\\-*/]\\s*(\\d+d\\d+|\\d+))*(\\s+[a-zA-Z0-9 ]+)?$")) {
      try {
        String[] splitMessage = rawMessage.split("#", 2);
        int repetitions = 1;
        String expressionPart;
        String optionalName = "";

        // Separa a quantidade de repetições e a expressão
        if (splitMessage.length > 1) {
          repetitions = Integer.parseInt(splitMessage[0]);
          expressionPart = splitMessage[1];
        } else {
          expressionPart = splitMessage[0];
        }

        // Divide a expressão e o nome opcional
        int lastSpaceIndex = expressionPart.lastIndexOf(' ');
        String expression = expressionPart;
        if (lastSpaceIndex > 0) {
          String possibleName = expressionPart.substring(lastSpaceIndex + 1).trim();
          if (!possibleName.matches("\\d+d\\d+|\\d+|[+\\-*/]")) {
            expression = expressionPart.substring(0, lastSpaceIndex).trim();
            optionalName = possibleName;
          }
        }

        List<String> parsedExpressions = parseDiceExpression(expression);

        // Valida operadores consecutivos
        for (int i = 1; i < parsedExpressions.size(); i++) {
          if ("+-*/".contains(parsedExpressions.get(i)) && "+-*/".contains(parsedExpressions.get(i - 1))) {
            event.getChannel().sendMessage("Formato inválido: operadores consecutivos não são permitidos.").queue();
            return;
          }
        }

        EmbedBuilder resultEmbed = new EmbedBuilder();
        resultEmbed.setColor(Color.orange);
        List<String> formattedResults = new ArrayList<>();

        // Processa as repetições
        for (int repetition = 0; repetition < repetitions; repetition++) {
          Integer runningTotal = null;
          StringBuilder rollDetails = new StringBuilder();

          for (int i = 0; i < parsedExpressions.size(); i++) {
            String currentToken = parsedExpressions.get(i).trim();

            if (currentToken.matches("\\d+d\\d+")) {
              // Processa dados
              String[] diceComponents = currentToken.split("d");
              int diceQuantity = Integer.parseInt(diceComponents[0]);
              int diceSides = Integer.parseInt(diceComponents[1]);

              List<Integer> rollResults = IntStream.range(0, diceQuantity)
                  .mapToObj(roll -> new Random().nextInt(diceSides) + 1)
                  .collect(Collectors.toList());
              int rollSum = rollResults.stream().mapToInt(Integer::intValue).sum();

              rollDetails.append(currentToken).append(": ").append(rollResults).append(" ");
              runningTotal = (runningTotal == null)
                  ? rollSum
                  : applyOperator(parsedExpressions.get(i - 1), runningTotal, rollSum);

            } else if (currentToken.matches("\\d+")) {
              // Processa valores numéricos
              int numericValue = Integer.parseInt(currentToken);
              rollDetails.append(numericValue).append(" ");
              runningTotal = (runningTotal == null)
                  ? numericValue
                  : applyOperator(parsedExpressions.get(i - 1), runningTotal, numericValue);

            } else if (!"+-*/".contains(currentToken)) {
              // Operador inválido
              event.getChannel().sendMessage("Operador inválido: " + currentToken).queue();
              return;
            }
          }

          if (runningTotal != null) {
            formattedResults.add("` " + runningTotal + " ` ⟵ " + rollDetails.toString().trim());
          } else {
            event.getChannel().sendMessage("Expressão inválida. Verifique sua entrada.").queue();
            return;
          }
        }


        resultEmbed.addField("Jogador:", event.getAuthor().getAsMention(), true);
        if (!optionalName.isEmpty()) {
          resultEmbed.addField("Dado:", optionalName, false);
        }
        resultEmbed.addField("Resultados:", String.join("\n", formattedResults), false);
        event.getChannel().sendMessageEmbeds(resultEmbed.build()).queue();
      } catch (NumberFormatException e) {
        event.getChannel().sendMessage("Erro ao interpretar os números. Use um formato válido como '1d10+2', '2#1d10+3', etc.").queue();
      }
    }
  }

  private int applyOperator(String operator, int leftOperand, int rightOperand) {
    return switch (operator) {
      case "+" -> leftOperand + rightOperand;
      case "-" -> leftOperand - rightOperand;
      case "*" -> leftOperand * rightOperand;
      case "/" -> rightOperand != 0 ? leftOperand / rightOperand : leftOperand;
      default -> throw new IllegalArgumentException("Operador inválido: " + operator);
    };
  }

  private List<String> parseDiceExpression(String expression) {
    List<String> parsedTokens = new ArrayList<>();
    Pattern tokenPattern = Pattern.compile("[+\\-*/]|\\d+d\\d+|\\d+");
    Matcher tokenMatcher = tokenPattern.matcher(expression);

    while (tokenMatcher.find()) {
      parsedTokens.add(tokenMatcher.group());
    }
    return parsedTokens;
  }
}

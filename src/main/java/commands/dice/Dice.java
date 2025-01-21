package commands.dice;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import commands.utils.*;

public class Dice {
  public static void diceCommand(MessageReceivedEvent event) {
    String message = event.getMessage().getContentRaw();
    try {
      String[] splitMessage = message.split("#", 2);
      long repetitions = 1;
      String expressionPart;
      String optionalName = "";

      // Separa a quantidade de repetições e a expressão
      if (splitMessage.length > 1) {
        repetitions = Long.parseLong(splitMessage[0].trim());
        expressionPart = splitMessage[1];
      } else {
        expressionPart = splitMessage[0];
      }

      // Divide expressão matemática e o nome usando regex
      Pattern pattern = Pattern.compile("^([\\d+d\\s\\-*/]+)([\\p{L}\\p{M}0-9\\- ]+)?$");
      Matcher expressionMatcher = pattern.matcher(expressionPart);

      String expression = expressionPart;
      if (expressionMatcher.find()) {
        expression = expressionMatcher.group(1).trim();
        if (expressionMatcher.group(2) != null) {
          optionalName = expressionMatcher.group(2).trim();
        }
      }

      List<String> parsedExpressions = DiceUtils.parseDiceExpression(expression);

      // Valida operadores consecutivos
      for (int i = 1; i < parsedExpressions.size(); i++) {
        if ("+-*/".contains(parsedExpressions.get(i)) && "+-*/".contains(parsedExpressions.get(i - 1))) {
          event.getChannel().sendMessage("Formato inválido: operadores consecutivos não são permitidos.").queue();
          return;
        }
      }

      EmbedBuilder resultEmbed = new EmbedBuilder();
      resultEmbed.setColor(Color.ORANGE);
      List<String> formattedResults = new ArrayList<>();

      // Processa as repetições
      for (int repetition = 0; repetition < repetitions; repetition++) {
        Long runningTotal = null;
        StringBuilder rollDetails = new StringBuilder();

        for (int i = 0; i < parsedExpressions.size(); i++) {
          String currentToken = parsedExpressions.get(i).replaceAll("\\s+", "").trim();

          if (currentToken.matches("\\d+d\\d+")) {
            // Processa dados
            String[] diceComponents = currentToken.split("d");
            long diceQuantity = Long.parseLong(diceComponents[0]);
            long diceSides = Long.parseLong(diceComponents[1]);

            List<Long> rollResults = LongStream.range(0, diceQuantity)
                    .mapToObj(roll -> new Random().nextLong(diceSides) + 1)
                    .collect(Collectors.toList());
            long rollSum = rollResults.stream().mapToLong(Long::longValue).sum();

            rollDetails.append(currentToken).append(": ").append(rollResults).append(" ");
            runningTotal = (runningTotal == null) ? rollSum : DiceUtils.applyOperator(parsedExpressions.get(i - 1), runningTotal, rollSum);

          } else if (currentToken.matches("\\d+")) {
            // Processa valores numéricos
            long numericValue = Long.parseLong(currentToken);
            rollDetails.append(numericValue).append(" ");
            runningTotal = (runningTotal == null) ? numericValue : DiceUtils.applyOperator(parsedExpressions.get(i - 1), runningTotal, numericValue);

          } else if (!"+-*/".contains(currentToken)) {
            // Operador inválido
            event.getChannel().sendMessage("Operador inválido: " + currentToken).queue();
            return;
          }
        }

        if (runningTotal != null) {
          formattedResults.add("` " + runningTotal + " ` -> " + rollDetails.toString().trim());
        } else {
          event.getChannel().sendMessage("Expressão inválida. Verifique sua entrada.").queue();
          return;
        }
      }

      resultEmbed.addField("Jogador:", event.getAuthor().getAsMention(), true);
      if (!optionalName.isEmpty()) {
        resultEmbed.addField("Nome:", optionalName, false);
      }
      resultEmbed.addField("Resultados:", String.join("\n", formattedResults), false);
      event.getChannel().sendMessageEmbeds(resultEmbed.build()).queue();

    } catch (NumberFormatException e) {
      event.getChannel().sendMessage("Erro ao interpretar os números. Use um formato válido como '1d10+2', '2#1d10+3', etc.").queue();
    }
  }
}

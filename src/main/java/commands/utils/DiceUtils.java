package commands.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceUtils {
  public static int applyOperator(String operator, int leftOperand, int rightOperand) {
    return switch (operator) {
      case "+" -> leftOperand + rightOperand;
      case "-" -> leftOperand - rightOperand;
      case "*" -> leftOperand * rightOperand;
      case "/" -> rightOperand != 0 ? leftOperand / rightOperand : leftOperand;
      default -> throw new IllegalArgumentException("Operador inválido: " + operator);
    };
  }

  public static List<String> parseDiceExpression(String expression) {
    List<String> parsedTokens = new ArrayList<>();
    Pattern tokenPattern = Pattern.compile("[+\\-*/]|\\d+d\\d+|\\d+");
    Matcher tokenMatcher = tokenPattern.matcher(expression);

    while (tokenMatcher.find()) {
      parsedTokens.add(tokenMatcher.group());
    }
    return parsedTokens;
  }
}

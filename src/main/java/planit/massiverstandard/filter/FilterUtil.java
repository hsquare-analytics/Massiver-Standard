package planit.massiverstandard.filter;

import java.util.List;

public class FilterUtil {

    public static List<String> operators = List.of("=", ">", "<", ">=", "<=", "<>", "!=");


    public static boolean compareValues(Object columnValue, String operator, String value) {
        double numericColumnValue;
        double numericValue;

        try {
            numericColumnValue = Double.parseDouble(columnValue.toString());
            numericValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return columnValue.toString().equals(value.replace("'", ""));
        }

        return switch (operator) {
            case ">" -> numericColumnValue > numericValue;
            case ">=" -> numericColumnValue >= numericValue;
            case "<" -> numericColumnValue < numericValue;
            case "<=" -> numericColumnValue <= numericValue;
            case "=" -> numericColumnValue == numericValue;
            case "!=" -> numericColumnValue != numericValue;
            default -> false;
        };
    }
}

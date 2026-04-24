package util;

/**
 * Input validation utility methods.
 */
public class Validator {

    /**
     * Check if a string is non-null and non-empty after trimming.
     */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if a numeric value is positive.
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Check if a year value is within valid range (1-6).
     */
    public static boolean isValidYear(int year) {
        return year >= 1 && year <= 6;
    }

    /**
     * Check if a string contains only letters and spaces.
     */
    public static boolean isAlphaWithSpaces(String value) {
        return value != null && value.matches("[a-zA-Z\\s]+");
    }
}

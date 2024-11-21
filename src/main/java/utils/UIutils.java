package utils;

public class UIutils {
    public static int tosquareIndex(String input) {
        int squareIndex = 0;
        squareIndex += ((input.charAt(0) - (int) 'a') * 8);
        squareIndex += ((Character.getNumericValue(input.charAt(1))) - 1);
        return squareIndex;
    }
}

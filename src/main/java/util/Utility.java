package util;

public class Utility {

    /**
     * Transfer a string matrix to an aligned string grid.
     * <p>
     * Precondition: all rows in matrix must have the same length.
     *
     * @param matrix    string matrix, should be String[rowsCount][colsCount]
     * @param separator separator between each column
     * @return the aligned string grid
     */
    public static String toGridString(String[][] matrix, String separator) {
        if (matrix.length == 0) return "";
        int colsCount = matrix[0].length;

        int[] colMaxWidth = new int[colsCount];  // max string lengths in each column
        for (int c = 0; c < colsCount; c++) {
            int width = 0;
            for (String[] row : matrix) {
                if (row[c] != null && row[c].length() > width)
                    width = row[c].length();
            }
            colMaxWidth[c] = width;
        }

        StringBuilder builder = new StringBuilder();
        for (String[] row : matrix) {
            for (int c = 0; c < colsCount; c++) {
                String s = row[c] == null ? "" : row[c];
                builder.append(s)
                        .append(" ".repeat(colMaxWidth[c] - s.length()));
                if (c != colsCount - 1) builder.append(separator);
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * Converts a double to a more readable string representation.
     *
     * @param value the double value
     * @return the string representation
     */
    public static String doubleToString(double value) {
        return String.format("%.4f", value);
    }
}

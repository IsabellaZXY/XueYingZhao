package data;

import util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A 2D table.
 * <p>
 * All indexes in this DataFrame are started from 0.
 */
public class DataFrame {

    /**
     * These two lists store the same cells, just in different order.
     */
    private final List<DataRow> rows;
    private final List<DataColumn> columns;

    private DataFrame(List<DataRow> rows, List<DataColumn> columns) {
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Creates a DataFrame reads from a csv file.
     *
     * @param csvName name of .csv file
     * @return the data frame created according to data stored in csv file
     * @throws IOException if file not readable
     */
    public static DataFrame fromCsv(String csvName) throws IOException {
        return fromCsv(new File(csvName));
    }

    /**
     * Creates a DataFrame reads from a csv file.
     *
     * @param csvFile .csv file
     * @return the data frame created according to data stored in csv file
     * @throws IOException if file not readable
     */
    public static DataFrame fromCsv(File csvFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            List<DataRow> rows = new ArrayList<>();
            List<String> titleRow = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].strip();
                if (titleRow.isEmpty()) {
                    for (String title : parts) {
                        String stripped = title.strip();
                        if (stripped.startsWith("\"") && stripped.endsWith("\""))
                            stripped = stripped.substring(1, stripped.length() - 1);
                        titleRow.add(stripped);
                    }
                } else {
                    List<DataCell> row = new ArrayList<>();
                    for (String part : parts) {
                        DataCell cell = new DataCell(part);
                        row.add(cell);
                    }
                    rows.add(new DataRow(row));
                }
            }
            return fromRows(titleRow, rows);
        }
    }

    /**
     * Creates a DataFrame from a list of columns.
     * <p>
     * A ll columns must have same length.
     *
     * @param columns list of columns
     * @return the newly created DataFrame
     */
    public static DataFrame fromColumns(List<DataColumn> columns) {
        List<DataRow> rows = new ArrayList<>();
        int colsCount = columns.size();
        if (colsCount == 0) throw new DataFrameException("Cannot create dataframe from 0 columns.");
        int rowsCount = columns.get(0).size();
        for (int c = 1; c < colsCount; c++) {
            if (columns.get(c).size() != rowsCount) {
                throw new DataFrameException(
                        "Column '" + c + "' has different number of data compares to the previous column.");
            }
        }
        for (int r = 0; r < rowsCount; r++) {
            List<DataCell> row = new ArrayList<>();
            for (DataColumn column : columns) {
                row.add(column.get(r));
            }
            rows.add(new DataRow(row));
        }
        return new DataFrame(rows, new ArrayList<>(columns));  // avoids modification to original columns
    }

    /**
     * Creates a DataFrame from a list of column titles and a list of data rows.
     * <p>
     * Precondition:
     * All rows must have same length, including the title row
     *
     * @param titleRow list of column titles
     * @param rows     list of data rows
     * @return the newly created DataFrame
     */
    public static DataFrame fromRows(List<String> titleRow, List<DataRow> rows) {
        List<List<DataCell>> columns = new ArrayList<>();
        int colsCount = titleRow.size();
        for (int c = 0; c < colsCount; c++) {
            columns.add(new ArrayList<>());
        }
        for (DataRow row : rows) {
            if (row.size() != colsCount) throw new DataFrameException(
                    "Row '" + row + "' has different number of data compares to the previous row.");
            for (int c = 0; c < colsCount; c++) {
                columns.get(c).add(row.get(c));
            }
        }
        List<DataColumn> dataColumns = new ArrayList<>();
        for (int c = 0; c < colsCount; c++) {
            dataColumns.add(new DataColumn(titleRow.get(c), columns.get(c)));
        }
        return new DataFrame(new ArrayList<>(rows), dataColumns);  // avoids modification to original rows
    }

    /**
     * Creates a DataFrame from java data.
     *
     * @param titles     string array of column titles
     * @param dataMatrix 2d matrix of data
     * @return the newly created DataFrame
     */
    public static DataFrame fromDataArray(String[] titles, Object[][] dataMatrix) {
        List<DataRow> rows = new ArrayList<>();
        for (Object[] rowData : dataMatrix) {
            List<DataCell> row = new ArrayList<>();
            for (Object data : rowData) {
                DataCell cell;
                if (data instanceof String) cell = new DataCell((String) data);
                else if (data instanceof Number) cell = new DataCell(((Number) data).doubleValue());
                else throw new DataFrameException("Cannot convert '" + data + "' to data cell.");
                row.add(cell);
            }
            rows.add(new DataRow(row));
        }
        return fromRows(List.of(titles), rows);
    }

    /**
     * Returns the row at <code>index</code>, starts from 0.
     * <p>
     * This method cannot get column titles, i.e. the row at index 0 is the first data row, not the titles.
     * <p>
     * Equivalent to dataframe[index,] in R.
     *
     * @param index the index, starts from 0
     * @return the row at given index
     */
    public DataRow getRow(int index) {
        return rows.get(index);
    }

    /**
     * Returns the column at <code>index</code>, starts from 0.
     * <p>
     * Equivalent to dataframe[,index] in R.
     *
     * @param index the index, starts from 0
     * @return the column at given index
     */
    public DataColumn getColumn(int index) {
        return columns.get(index);
    }

    /**
     * @return the list of column titles
     */
    public List<String> getTitles() {
        List<String> titles = new ArrayList<>();
        for (DataColumn column : columns) {
            titles.add(column.getColName());
        }
        return titles;
    }

    /**
     * Returns the column that has title exactly equals to <code>colName</code>.
     *
     * @param colName the name of the column to look for
     * @return the column that has that name
     * @throws DataFrameException if no column has that name
     */
    public DataColumn getColumn(String colName) throws DataFrameException {
        int index = indexOfColumn(colName);
        if (index < 0)
            throw new DataFrameException("No such column '" + colName + "'");
        return getColumn(index);
    }

    /**
     * Returns the index of the column in this DataFrame that has title equals to <code>colName</code>, or -1
     * if not found.
     *
     * @param colName the name of the column to look for
     * @return index of that column, -1 if not found
     */
    public int indexOfColumn(String colName) {
        colName = colName.strip();
        for (int i = 0; i < columnsCount(); i++) {
            if (colName.equals(getColumn(i).getColName())) return i;
        }
        return -1;
    }

    /**
     * Returns the data cell at row <code>row</code>, column <code>column</code>.
     * <p>
     * Equivalent to dataframe[row,column] in R.
     *
     * @param row    the row index, starts from 0
     * @param column the column index, starts from 0
     * @return the cell at that position
     */
    public DataCell getCell(int row, int column) {
        return rows.get(row).get(column);
    }

    /**
     * Returns the data cell at row <code>row</code> and column that has title <code>colName</code>.
     *
     * @param row     row index, starts from 0
     * @param colName name of column to look for
     * @return the cell at that position
     * @throws DataFrameException if no such column exists
     */
    public DataCell getCell(int row, String colName) throws DataFrameException {
        DataColumn column = getColumn(colName);
        return column.get(row);
    }

    /**
     * Returns a copy of sub-DataFrame of this, with the same number of rows.
     * <p>
     * Mutating the parent DataFrame would not have any effect on the sub-DataFrame, and vice versa.
     *
     * @param columnIndexes indexes of columns to be copied
     * @return the copied sub-DataFrame
     */
    public DataFrame subFrameByColumns(int... columnIndexes) {
        List<DataColumn> columns = new ArrayList<>();
        for (int c : columnIndexes) {
            columns.add(getColumn(c).deepCopy());
        }
        return fromColumns(columns);
    }

    /**
     * Returns a copy of sub-DataFrame of this, with the same number of rows.
     * <p>
     * Every column between <code>from</code> and <code>to</code> is copied to the new DataFrame.
     * <p>
     * Mutating the parent DataFrame would not have any effect on the sub-DataFrame, and vice versa.
     *
     * @param from the beginning index of columns to be copied, inclusive
     * @param to   the ending index of columns to be copied, exclusive
     * @return the copied sub-DataFrame
     */
    public DataFrame subFrameByColumnsRange(int from, int to) {
        int[] indexes = new int[to - from];
        for (int i = 0; i < indexes.length; i++) indexes[i] = i + from;
        return subFrameByColumns(indexes);
    }

    /**
     * Returns a copy of sub-DataFrame of this, with the same number of rows.
     * <p>
     * Mutating the parent DataFrame would not have any effect on the sub-DataFrame, and vice versa.
     *
     * @param columnNames titles of columns to be copied
     * @return the copied sub-DataFrame
     */
    public DataFrame subFrameByColumns(String... columnNames) {
        int[] indexes = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            int index = indexOfColumn(columnNames[i]);
            if (index < 0)
                throw new DataFrameException("No such column '" + columnNames[i] + "'");
            indexes[i] = index;
        }
        return subFrameByColumns(indexes);
    }

    /**
     * Returns a copy of sub-DataFrame of this, with the same number of columns.
     * <p>
     * Mutating the parent DataFrame would not have any effect on the sub-DataFrame, and vice versa.
     *
     * @param rowIndexes indexes of rows to be copied
     * @return the copied sub-DataFrame
     */
    public DataFrame subFrameByRows(int... rowIndexes) {
        List<DataRow> rows = new ArrayList<>();
        for (int r : rowIndexes) {
            rows.add(getRow(r).deepCopy());
        }
        return fromRows(getTitles(), rows);
    }

    /**
     * Returns a copy of sub-DataFrame of this, with the same number of columns.
     * <p>
     * Every row between <code>from</code> and <code>to</code> is copied to the new DataFrame.
     * <p>
     * Mutating the parent DataFrame would not have any effect on the sub-DataFrame, and vice versa.
     *
     * @param from the beginning index of rows to be copied, inclusive
     * @param to   the ending index of rows to be copied, exclusive
     * @return the copied sub-DataFrame
     */
    public DataFrame subFrameByRowsRange(int from, int to) {
        int[] indexes = new int[to - from];
        for (int i = 0; i < indexes.length; i++) indexes[i] = i + from;
        return subFrameByRows(indexes);
    }

    /**
     * Returns a new DataFrame with any non-numeric rows removed.
     * <p>
     * Any rows with at least 1 non-numeric value at numeric column are removed.
     *
     * @param preservedColNames the name of columns that does not count as non-numeric.
     *                          If some row has a non-numeric value at column in <code>preservedColNames</code>, it
     *                          would not be removed.
     * @return a new DataFrame any non-numeric rows removed
     */
    public DataFrame numericSubFrame(String... preservedColNames) {
        List<DataRow> numericRows = new ArrayList<>();
        List<String> titles = getTitles();
        String[] fixedColNames = new String[preservedColNames.length];  // stripped version
        for (int i = 0; i < preservedColNames.length; i++) {
            fixedColNames[i] = preservedColNames[i].strip();
        }
        OUT_LOOP:
        for (DataRow row : rows) {
            for (int c = 0; c < row.size(); c++) {
                DataCell cell = row.get(c);
                String thisTitle = titles.get(c);
                for (String preserved : fixedColNames) {
                    if (preserved.equals(thisTitle)) break;
                }
                if (!cell.isNumber())
                    continue OUT_LOOP;
            }
            numericRows.add(row.deepCopy());
        }
        return fromRows(getTitles(), numericRows);
    }

    /**
     * @return an independent copy of this
     */
    public DataFrame copy() {
        List<DataRow> newRows = new ArrayList<>();
        for (DataRow row : this.rows) {
            newRows.add(row.deepCopy());
        }
        return fromRows(getTitles(), newRows);
    }

    /**
     * Returns a copy of this DataFrame, with all NA replaced by <code>replacement</code>
     *
     * @param replacement numeric replacement
     * @return the new DataFrame with all NA replaced
     */
    public DataFrame replaceNa(double replacement) {
        DataFrame copied = copy();
        for (DataRow row : copied.rows) {
            for (DataCell cell : row) {
                if (cell.isNa()) cell.setValue(replacement);
            }
        }
        return copied;
    }

    /**
     * Returns a copy of this DataFrame, with all NA replaced by <code>replacement</code>
     *
     * @param replacement string replacement
     * @return the new DataFrame with all NA replaced
     */
    public DataFrame replaceNa(String replacement) {
        DataFrame copied = copy();
        for (DataRow row : copied.rows) {
            for (DataCell cell : row) {
                if (cell.isNa()) cell.setValue(replacement);
            }
        }
        return copied;
    }

    /**
     * Reshape this DataFrame to pivot-longer form.
     *
     * @param preservedColNames name of columns that do not modify
     * @param namesTo           the name of new column containing categorical names
     * @param valuesTo          the name of new column containing values
     * @return the reshaped DataFrame
     */
    public DataFrame pivotLonger(List<String> preservedColNames, String namesTo, String valuesTo) {
        List<String> preserved = new ArrayList<>();  // strips all preserved names
        for (String s : preservedColNames) {
            preserved.add(s.strip());
        }

        int repetition = columnsCount() - preserved.size();

        List<DataColumn> fixedColumns = new ArrayList<>();
        List<DataColumn> movingColumns = new ArrayList<>();
        for (String colName : getTitles()) {
            if (preserved.contains(colName)) {  // fixed column
                DataColumn oldColumn = getColumn(colName);
                List<DataCell> newColumn = new ArrayList<>();
                for (int r = 0; r < rowsCount(); r++) {
                    for (int i = 0; i < repetition; i++) {
                        newColumn.add(oldColumn.get(r).copy());
                    }
                }
                fixedColumns.add(new DataColumn(colName, newColumn));
            } else {  // moving column
                movingColumns.add(getColumn(colName));
            }
        }

        List<DataCell> namesColumn = new ArrayList<>();
        List<DataCell> valuesColumn = new ArrayList<>();
        for (int r = 0; r < rowsCount(); r++) {  // reshape the data
            for (DataColumn movingColumn : movingColumns) {  // distribute data in one row to different rows
                namesColumn.add(new DataCell(movingColumn.getColName()));
                valuesColumn.add(movingColumn.get(r).copy());
            }
        }

        List<DataColumn> newColumns = new ArrayList<>(fixedColumns);
        newColumns.add(new DataColumn(namesTo, namesColumn));
        newColumns.add(new DataColumn(valuesTo, valuesColumn));
        return fromColumns(newColumns);
    }

    /**
     * Number of data rows, does not include title row.
     *
     * @return umber of data rows, does not include title row
     */
    public int rowsCount() {
        return rows.size();
    }

    /**
     * Number of columns.
     *
     * @return number of columns
     */
    public int columnsCount() {
        return columns.size();
    }

    @Override
    public String toString() {
        int rowsCount = rowsCount();
        int colsCount = columnsCount();
        String[][] matrix = new String[rowsCount + 1][colsCount];
        for (int c = 0; c < colsCount; c++) {
            matrix[0][c] = getColumn(c).getColName();
        }
        for (int r = 0; r < rowsCount; r++) {
            DataRow row = getRow(r);
            for (int c = 0; c < colsCount; c++) {
                matrix[r + 1][c] = row.get(c).toString();
            }
        }
        return Utility.toGridString(matrix, ", ");
    }
}

package data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data column.
 */
public class DataColumn extends LinearDataList {

    private final String colName;

    /**
     * The constructor.
     *
     * @param colName title of this column
     * @param columnData data cells in this column
     */
    public DataColumn(String colName, List<DataCell> columnData) {
        super(columnData);
        this.colName = colName;
    }

    @Override
    public DataColumn deepCopy() {
        List<DataCell> cells = new ArrayList<>();
        for (DataCell cell : this) {
            cells.add(cell.copy());
        }
        return new DataColumn(colName, cells);
    }

    /**
     * @return the title of this column
     */
    public String getColName() {
        return colName;
    }

    @Override
    public String toString() {
        return "DataColumn{" + colName + ": " + cellList.toString() + "}";
    }
}

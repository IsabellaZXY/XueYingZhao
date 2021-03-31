package data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data row.
 */
public class DataRow extends LinearDataList {

    /**
     * The constructor.
     *
     * @param cells list of cells in this row
     */
    public DataRow(List<DataCell> cells) {
        super(cells);
    }

    @Override
    public DataRow deepCopy() {
        List<DataCell> cells = new ArrayList<>();
        for (DataCell cell : this) {
            cells.add(cell.copy());
        }
        return new DataRow(cells);
    }

    @Override
    public String toString() {
        return "DataRow" + cellList.toString();
    }
}

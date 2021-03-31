package data;

import java.util.Iterator;
import java.util.List;

/**
 * A list of data cells, can be a row or a column.
 * <p>
 * This list should be immutable once it is created. Data modification should happen
 * inside DataCell via DataCell.setValue().
 */
public abstract class LinearDataList implements Iterable<DataCell> {

    protected final List<DataCell> cellList;

    LinearDataList(List<DataCell> cellList) {
        this.cellList = cellList;
    }

    @Override
    public Iterator<DataCell> iterator() {
        return cellList.iterator();
    }

    /**
     * Returns a copy of this instance, with all values also copied.
     *
     * @return a copy of this instance, with all values also copied
     */
    public abstract LinearDataList deepCopy();

    /**
     * Converts this LinearDataList to an array of doubles.
     * <p>
     * Non-numeric values are converted to Double.NaN
     *
     * @return the numeric values array
     */
    public double[] toNumberArray() {
        double[] arr = new double[cellList.size()];
        for (int i = 0; i < arr.length; i++) {
            DataCell cell = cellList.get(i);
            if (cell.isNumber()) arr[i] = cell.getNumberValue();
        }
        return arr;
    }

    public DataCell get(int index) {
        return cellList.get(index);
    }

    /**
     * @return the size of this
     */
    public int size() {
        return cellList.size();
    }

    /**
     * @return the number of numeric values in this LinearDataList
     */
    public int numericCellsCount() {
        int count = 0;
        for (DataCell cell : cellList) {
            if (cell.isNumber()) count++;
        }
        return count;
    }

    /**
     * @return the sum of all numeric values in this LinearDataList
     */
    public double sum() {
        double res = 0.0;
        for (DataCell cell : cellList) {
            if (cell.isNumber()) res += cell.getNumberValue();
        }
        return res;
    }

    /**
     * @return the mean value of all numeric values in this LinearDataList
     */
    public double mean() {
        double sum = sum();
        return sum / numericCellsCount();
    }

    /**
     * @return the minimum value of all numeric values in this LinearDataList
     */
    public double min() {
        double res = Double.MAX_VALUE;
        for (DataCell cell : cellList) {
            if (cell.isNumber()) {
                double cellValue = cell.getNumberValue();
                if (cellValue < res) res = cellValue;
            }
        }
        return res;
    }

    /**
     * @return the maximum value of all numeric values in this LinearDataList
     */
    public double max() {
        double res = -Double.MAX_VALUE;
        for (DataCell cell : cellList) {
            if (cell.isNumber()) {
                double cellValue = cell.getNumberValue();
                if (cellValue > res) res = cellValue;
            }
        }
        return res;
    }
}

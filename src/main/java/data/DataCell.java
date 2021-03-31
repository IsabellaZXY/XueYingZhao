package data;

import java.util.Objects;

/**
 * A data cell.
 */
public class DataCell {
    private String value;
    private double numberValue;

    private DataCell() {
    }

    DataCell(String value) {
        setValue(value);
    }

    DataCell(double numberValue) {
        setValue(numberValue);
    }

    /**
     * @return a copy of this
     */
    public DataCell copy() {
        DataCell cell = new DataCell();
        cell.value = this.value;
        cell.numberValue = this.numberValue;
        return cell;
    }

    /**
     * Sets the value wrapped by this DataCell.
     *
     * @param value the string value
     */
    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        // strips '"' and '"'
        if (value.startsWith("\"") && value.endsWith("\"")) this.value = value.substring(1, value.length() - 1);
        else this.value = value;
        try {
            numberValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            numberValue = Double.NaN;
        }
    }

    /**
     * Sets the value wrapped by this DataCell.
     *
     * @param numberValue the double value
     */
    public void setValue(double numberValue) {
        if (Double.isNaN(numberValue)) {
            this.value = "";
        } else {
            this.value = String.valueOf(numberValue);
        }
        this.numberValue = numberValue;
    }

    /**
     * @return whether the value in this cell is NA
     */
    public boolean isNa() {
        return value.isEmpty();
    }

    /**
     * @return whether the value contained in this DataCell is numeric
     */
    public boolean isNumber() {
        return !Double.isNaN(numberValue);
    }

    /**
     * @return the numeric value contained in this DataCell, or Double.NaN if this DataCell is not numeric
     */
    public double getNumberValue() {
        return numberValue;
    }

    @Override
    public String toString() {
        return value.isEmpty() ? "NA" : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataCell cell = (DataCell) o;

        return Objects.equals(value, cell.value);
    }
}

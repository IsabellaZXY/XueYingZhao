package lm;

import data.DataColumn;
import data.DataFrame;
import util.Utility;

/**
 * The model for simple linear regression.
 */
public class SimpleLinearModel {

    protected final String predictor;
    protected final double[] estimates;
    protected final double[] stdErrors;
    protected final double[] tValues;
    private final DataColumn responseCol;
    private final DataColumn predictorCol;
    protected String response;
    protected double correlation;
    protected double rSquared;
    protected double rSquaredAdj;
    protected double rss;  // residual squared sum
    protected double mse;
    protected double rse;  // residual standard error
    protected double sst;  // sum squared total
    protected double ssReg;
    protected int n;  // number of data

    /**
     * Constructs a simple linear regression model.
     *
     * @param dataFrame the data frame
     * @param response  the name of the response variable
     * @param predictor the name of the predictor variable
     */
    public SimpleLinearModel(DataFrame dataFrame, String response, String predictor) {
        this.response = response;
        this.predictor = predictor;

        this.estimates = new double[2];  // Intercept and slope
        this.stdErrors = new double[2];
        this.tValues = new double[2];

        DataFrame usedFrame = dataFrame.subFrameByColumns(response, predictor);
        DataFrame numericFrame = usedFrame.numericSubFrame();  // make sure all rows are numeric

        this.responseCol = numericFrame.getColumn(response);
        this.predictorCol = numericFrame.getColumn(predictor);

        analyze();
        analyzeAnova();
    }

    /**
     * @return the summary string
     */
    public String summary() {
        String[][] matrix = new String[estimates.length + 1][4];
        matrix[0][0] = "";
        matrix[0][1] = "Estimate";
        matrix[0][2] = "Std. Error";
        matrix[0][3] = "t-value";

        matrix[1][0] = "Intercept";
        matrix[2][0] = predictor;

        for (int r = 0; r < estimates.length; r++) {
            matrix[r + 1][1] = Utility.doubleToString(estimates[r]);
            matrix[r + 1][2] = Utility.doubleToString(stdErrors[r]);
            matrix[r + 1][3] = Utility.doubleToString(tValues[r]);
        }

        return "Coefficients:\n" +
                Utility.toGridString(matrix, " ") +
                "\nResidual standard error: " +
                Utility.doubleToString(rse) +
                " on " +
                degreesOfFreedom() +
                " degrees of freedom.\n" +
                "Multiple R-squared: " +
                Utility.doubleToString(rSquared) +
                ", Adjusted R-squared: " +
                Utility.doubleToString(rSquaredAdj) +
                "\nF-statistics: " +
                Utility.doubleToString(ssReg / mse) +
                " on " +
                degreesOfFreedom() +
                " degrees of freedom.";
    }

    @Override
    public String toString() {
        return "SimpleLinearModel{\n" + summary() + "\n}";
    }

    /**
     * @return the string representation of anova table
     */
    public String anova() {
        StringBuilder builder = new StringBuilder()
                .append("Response: ")
                .append(response)
                .append('\n');
        String[][] matrix = new String[3][5];
        matrix[0][0] = "";
        matrix[0][1] = "df";
        matrix[0][2] = "Sum Sq.";
        matrix[0][3] = "Mean Sq.";
        matrix[0][4] = "F value";

        matrix[1][0] = predictor;
        matrix[1][1] = "1";
        String s = Utility.doubleToString(ssReg);
        matrix[1][2] = s;
        matrix[1][3] = s;
        matrix[1][4] = Utility.doubleToString(ssReg / mse);

        matrix[2][0] = "Residual";
        matrix[2][1] = String.valueOf(degreesOfFreedom());
        matrix[2][2] = Utility.doubleToString(rss);
        matrix[2][3] = Utility.doubleToString(mse);

        builder.append(Utility.toGridString(matrix, " "));

        return builder.toString();
    }

    /**
     * This method should be called in the constructor of any child class, after data analysis finished
     */
    protected void analyzeAnova() {
        rse = Math.sqrt(mse);
        rSquaredAdj = 1.0 - (rss / degreesOfFreedom()) / (sst / (n - 1));
        for (int r = 0; r < estimates.length; r++) {
            tValues[r] = estimates[r] / stdErrors[r];
        }
    }

    /**
     * @return the degrees of freedom (df)
     */
    public int degreesOfFreedom() {
        return n - 2;
    }

    private void analyze() {
        double yMean = responseCol.mean();  // y_bar
        double xMean = predictorCol.mean();  // x_bar
        n = responseCol.size();

        double sxx = 0.0;
        double sxy = 0.0;
        double syy = 0.0;

        // Least Squared Sum
        // Sxx = sum((x_bar - x_i)^2)
        // Syy = sum((y_bar - y_i)^2)
        // Sxy = sum((x_bar - x_i) * (y_bar - y_i))
        for (int i = 0; i < n; i++) {
            double xi = predictorCol.get(i).getNumberValue();
            double yi = responseCol.get(i).getNumberValue();

            double xDiff = xi - xMean;
            double yDiff = yi - yMean;

            sxx += Math.pow(xDiff, 2);
            syy += Math.pow(yDiff, 2);
            sxy += xDiff * yDiff;
        }
        sst = syy;

        double slope = sxy / sxx;  // estimated b1
        double intercept = yMean - slope * xMean;  // estimated b0
        estimates[1] = slope;
        estimates[0] = intercept;
        correlation = sxy / Math.sqrt(sxx * syy);  // r
        rSquared = Math.pow(correlation, 2);  // R^2

        rss = 0.0;  // residual squared sum
        ssReg = 0.0;  // regression total squares
        for (int i = 0; i < n; i++) {
            if (!predictorCol.get(i).isNumber() || !responseCol.get(i).isNumber()) continue;
            double xi = predictorCol.get(i).getNumberValue();
            double yi = responseCol.get(i).getNumberValue();

            double predict = intercept + slope * xi;
            double residual = yi - predict;
            double reg = yMean - predict;
            rss += Math.pow(residual, 2);
            ssReg += Math.pow(reg, 2);
        }
        mse = rss / degreesOfFreedom();

        stdErrors[1] = Math.sqrt(mse / sxx);  // standard error of slope estimator
        stdErrors[0] = Math.sqrt(mse * (1.0 / n + Math.pow(xMean, 2) / sxx));  // standard error of intercept estimator
    }
}

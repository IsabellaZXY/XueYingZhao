package lm;

import data.DataFrame;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LmTest {

    /**
     * Expected output:
     * Coefficients:
     * Estimate Std. Error t-value
     * Intercept     7.6849   0.1599     48.0487
     * starfleet_gpa -0.1031  0.0210     -4.9219
     * <p>
     * Residual standard error: 1.1268 on 3010 degrees of freedom.
     * Multiple R-squared: 0.0080, Adjusted R-squared: 0.0077
     * F-statistics: 24.2246 on 3010 degrees of freedom.
     * Response: perseverance_score
     * df   Sum Sq.   Mean Sq. F value
     * starfleet_gpa 1    30.7581   30.7581  24.2246
     * Residual      3010 3821.8098 1.2697
     */
    @Test
    void testSimpleLmShip() throws IOException {
        DataFrame df = DataFrame.fromCsv("data/ship_data.csv");
        SimpleLinearModel lm = new SimpleLinearModel(df, "perseverance_score", "starfleet_gpa");
        System.out.println(lm.summary());
        System.out.println(lm.anova());
    }

    /**
     * Expected output:
     * Coefficients:
     * Estimate    Std. Error t-value
     * Intercept 130923.9434 22095.4533 5.9254
     * list      0.8725      0.0151     57.7459
     * <p>
     * Residual standard error: 102217.2727 on 163 degrees of freedom.
     * Multiple R-squared: 0.9534, Adjusted R-squared: 0.9531
     * F-statistics: 3334.5923 on 163 degrees of freedom.
     * Response: Sale
     * df  Sum Sq.             Mean Sq.            F value
     * list     1   34841056545568.5860 34841056545568.5860 3334.5923
     * Residual 163 1703084445062.6204  10448370828.6050
     */
    @Test
    void testSimpleLmReale() throws IOException {
        DataFrame df = DataFrame.fromCsv("data/reale_data.csv");
        SimpleLinearModel lm = new SimpleLinearModel(df, "Sale", "list");
        System.out.println(lm.summary());
        System.out.println(lm.anova());
    }
}

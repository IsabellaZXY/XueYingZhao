package data;

import lm.SimpleLinearModel;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

public class DataTest {

    @Test
    void testDataFrameFromArray() {
        DataFrame df = DataFrame.fromDataArray(
                new String[]{"name", "category", "gender", "hp", "attack range"},
                new Object[][]{
                        {"Garen", "fighter", "M", 616.0, 125},
                        {"Annie", "mage", "F", 511.0, 625},
                        {"Master Yi", "fighter", "M", 598.0, 125},
                        {"Caitlyn", "marksman", "F", 524.0, 650}
                }
        );
        System.out.println(df);
    }

    @Test
    void testDataTidy() {
        DataFrame df = DataFrame.fromDataArray(
                new String[]{"name", "category", "gender", "hp", "attack range"},
                new Object[][]{
                        {"Garen", "fighter", "M", 616.0, 125},
                        {"Annie", "mage", "F", 511.0, 625},
                        {"Master Yi", "fighter", "M", 598.0, 125},
                        {"Caitlyn", "marksman", "F", 524.0, 650}
                }
        );
        DataFrame tidy = df.pivotLonger(List.of("name", "category", "gender"), "item", "value");
        System.out.println(tidy);
    }

    @Test
    void testMutation() {
        DataFrame df = DataFrame.fromDataArray(
                new String[]{"name", "category", "gender", "hp", "attack range"},
                new Object[][]{
                        {"Garen", "fighter", "M", 616.0, 125},
                        {"Annie", "mage", "F", 511.0, 625},
                        {"Master Yi", "fighter", "M", 598.0, 125},
                        {"Caitlyn", "marksman", "F", 524.0, 650}
                }
        );
        DataFrame df2 = df.subFrameByColumns(0, 1, 3);
        df2.getCell(0, "hp").setValue(700.0);
        // the value in the original DataFrame should not change
        assert df.getCell(0, "hp").getNumberValue() == 616.0;
    }

    @Test
    void testSimpleLm2() throws IOException {
        DataFrame df = DataFrame.fromCsv("data/ship_data.csv");
        SimpleLinearModel lm = new SimpleLinearModel(df, "perseverance_score", "starfleet_gpa");
        System.out.println(lm.summary());
        System.out.println(lm.anova());
    }

    @Test
    void testSimpleLm() throws IOException {
        DataFrame df = DataFrame.fromCsv("data/reale_data.csv");
        SimpleLinearModel lm = new SimpleLinearModel(df, "Sale", "list");
        System.out.println(lm.summary());
        System.out.println(lm.anova());
    }
}

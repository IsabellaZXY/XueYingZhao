package data;

import org.junit.jupiter.api.Test;

import java.util.List;

public class DataTest {

    /**
     * Expected output:
     * name     , category, gender, position, hp   , attack range
     * Garen    , fighter , M     , top     , 616.0, 125.0
     * Annie    , mage    , F     , mid     , 511.0, 625.0
     * Master Yi, fighter , M     , jungle  , 598.0, 125.0
     * Caitlyn  , marksman, F     , bottom  , 524.0, 650.0
     */
    @Test
    void testDataFrameFromArray() {
        DataFrame df = DataFrame.fromDataArray(
                new String[]{"name", "category", "gender", "position", "hp", "attack range"},
                new Object[][]{
                        {"Garen", "fighter", "M", "top", 616.0, 125},
                        {"Annie", "mage", "F", "mid", 511.0, 625},
                        {"Master Yi", "fighter", "M", "jungle", 598.0, 125},
                        {"Caitlyn", "marksman", "F", "bottom", 524.0, 650}
                }
        );
        System.out.println(df);
    }

    /**
     * Expected output:
     * name     , category, gender, item        , value
     * Garen    , fighter , M     , position    , top
     * Garen    , fighter , M     , hp          , 616.0
     * Garen    , fighter , M     , attack range, 125.0
     * Annie    , mage    , F     , position    , mid
     * Annie    , mage    , F     , hp          , 511.0
     * Annie    , mage    , F     , attack range, 625.0
     * Master Yi, fighter , M     , position    , jungle
     * Master Yi, fighter , M     , hp          , 598.0
     * Master Yi, fighter , M     , attack range, 125.0
     * Caitlyn  , marksman, F     , position    , bottom
     * Caitlyn  , marksman, F     , hp          , 524.0
     * Caitlyn  , marksman, F     , attack range, 650.0
     */
    @Test
    void testDataTidy() {
        DataFrame df = DataFrame.fromDataArray(
                new String[]{"name", "category", "gender", "position", "hp", "attack range"},
                new Object[][]{
                        {"Garen", "fighter", "M", "top", 616.0, 125},
                        {"Annie", "mage", "F", "mid", 511.0, 625},
                        {"Master Yi", "fighter", "M", "jungle", 598.0, 125},
                        {"Caitlyn", "marksman", "F", "bottom", 524.0, 650}
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
}

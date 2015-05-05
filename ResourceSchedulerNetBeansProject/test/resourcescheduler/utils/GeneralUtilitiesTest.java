package resourcescheduler.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class GeneralUtilitiesTest {

    public GeneralUtilitiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGenerateRandomNumber() {
        System.out.println("testGenerateRandomNumber");

        int lowerbound = 0;
        int upperbound = 0;

        int result = GeneralUtilities.generateRandomNumber(lowerbound, upperbound);
        assertEquals(result, 0);

        lowerbound = Integer.MIN_VALUE;
        upperbound = 0;
        result = GeneralUtilities.generateRandomNumber(lowerbound, upperbound);
        if (result > 0) {
            fail("Result must not be >0");
        }

        lowerbound = 0;
        upperbound = Integer.MAX_VALUE;
        result = GeneralUtilities.generateRandomNumber(lowerbound, upperbound);
        if (result < 0) {
            fail("Result must not be <0");
        }

        lowerbound = -1;
        upperbound = 1;
        for (int i = 0; i < 10; i++) {
            result = GeneralUtilities.generateRandomNumber(lowerbound, upperbound);
            if (result > upperbound || result < lowerbound) {
                fail("Method does not work well");
            }
        }
    }
}

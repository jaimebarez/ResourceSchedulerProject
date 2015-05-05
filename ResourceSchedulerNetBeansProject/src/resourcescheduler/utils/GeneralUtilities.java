package resourcescheduler.utils;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class GeneralUtilities {

    public static int generateRandomNumber(int lowerbound, int upperbound) {
        /*Why 1d? Example: If upperbound is Integer.MAX_VALUE and lowerbound is 0,
         the (upperbound - lowerbound) + 1d operation experiences an Integer Overflow*/
        return Double.valueOf(Math.floor(Math.random()
                * ((upperbound - lowerbound) + 1d) + lowerbound))
                .intValue();
    }
}

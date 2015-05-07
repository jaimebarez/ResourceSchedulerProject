package resourcescheduler.utils;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class GeneralUtilities {

    public static int generateRandomNumber(int lowerbound, int upperbound) {
        /*Why 1f? Example: If upperbound is Integer.MAX_VALUE and lowerbound is 0,
         the (upperbound - lowerbound) + 1d operation experiences an Integer Overflow 
         and we don't get the expected calues*/
        return Double.valueOf(Math.floor(Math.random()
                * ((upperbound - lowerbound) + 1f/*Explanation above*/) + lowerbound))
                .intValue();
    }
}

package resourcescheduler.utils;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class GeneralUtilities {

    public static int generateRandomNumber(int min, int max) {
        return Double.valueOf(Math.floor(Math.random() * (max - min + 1) + min)).intValue();
    }
}

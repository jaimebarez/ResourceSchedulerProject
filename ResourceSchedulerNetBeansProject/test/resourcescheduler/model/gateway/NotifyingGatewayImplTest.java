package resourcescheduler.model.gateway;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.Message;
import static resourcescheduler.utils.GeneralUtilities.generateRandomNumber;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class NotifyingGatewayImplTest {

    @Test
    public void testCanConfigureDesiredResourcesQuantity() {
        System.out.println("testCanConfigureDesiredResourcesQuantity");

        CompleteGatewayImpl gw = new CompleteGatewayImpl();
        //0 is default value
        assertEquals(0, gw.getDesiredAvailableResources());

        //It can work with many numbers
        final List<Integer> exampleResourcesNumbers
                = Arrays.asList(1, 2, 10, 5, 0, 100, 0, Integer.MAX_VALUE, 43);

        for (int exampleResourcesNumber : exampleResourcesNumbers) {
            gw.setDesiredAvailableResources(exampleResourcesNumber);

            assertEquals(exampleResourcesNumber, gw.getDesiredAvailableResources());
        }

        //Random numbers test
        int[] exampleRandomResourcesNumbers = new int[10];
        for (int i = 0; i < exampleRandomResourcesNumbers.length; i++) {

            int randomNumber = generateRandomNumber(0, Integer.MAX_VALUE);
            gw.setDesiredAvailableResources(randomNumber);

            assertEquals(randomNumber, gw.getDesiredAvailableResources());
        }

        //Normal number
        int workingNumber = 350;
        gw.setDesiredAvailableResources(workingNumber);
        assertEquals(workingNumber, gw.getDesiredAvailableResources());

        //Negative numbers test
        try {
            gw.setDesiredAvailableResources(-1);
            //If you are below here, you did not throw an exception
            fail("Negative numbers are not allowed");
        } catch (IllegalArgumentException ex) {
            assertEquals(workingNumber, gw.getDesiredAvailableResources());
        }
    }

    public class CompleteGatewayImpl extends NotifyingGatewayImpl {

        @Override
        public void send(Message msg) {
        }
    }
}

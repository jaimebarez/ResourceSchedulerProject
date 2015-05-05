package resourcescheduler.model.gateway;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.Message;
import static resourcescheduler.utils.GeneralUtilities.generateRandomNumber;

/**
 *
 * @author jaime.barez.lobato
 */
public class CompleteGatewayTest {
    
    public CompleteGatewayTest() {
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
    public void testSend() {
    }

    @Test
    public void testAddOnMessageCompletedListener() {
    }

    @Test
    public void testRemoveOnMessageCompletedListener() {
    }

    @Test
    public void testAddAvailableResourcesListener() {
    }

    @Test
    public void testRemoveOnAvailableResourcesListener() {
    }

    @Test
    public void testGetAvailableResources() {
    }

    @Test
    public void testGetBusyResources() {
    }

    @Test
    public void testCanConfigureDesiredResourcesQuantity() {
        System.out.println("testCanConfigureDesiredResourcesQuantity");
        
        CompleteGatewayImpl completeGatewayImpl = new CompleteGatewayImpl();
        //0 is default value
        assertEquals(0, completeGatewayImpl.getDesiredAvailableResources());

        //It can work with many numbers
        final int[] exampleResourcesNumbers = new int[]{1, 2, 10, 5, 0, 100, 0};
        for (int exampleResourcesNumber : exampleResourcesNumbers) {
            completeGatewayImpl.setDesiredAvailableResources(exampleResourcesNumber);
            assertEquals(exampleResourcesNumber, completeGatewayImpl.getDesiredAvailableResources());
        }

        //Random numbers test
        int[] exampleRandomResourcesNumbers = new int[10];
        for (int i = 0; i < exampleRandomResourcesNumbers.length; i++) {

            int randomNumber = generateRandomNumber(0, Integer.MAX_VALUE);
            completeGatewayImpl.setDesiredAvailableResources(randomNumber);
            assertEquals(randomNumber, completeGatewayImpl.getDesiredAvailableResources());
        }

        //Negative numbers test
        int workingNumber = 350;
        completeGatewayImpl.setDesiredAvailableResources(workingNumber);
        assertEquals(workingNumber, completeGatewayImpl.getDesiredAvailableResources());
        try {
            completeGatewayImpl.setDesiredAvailableResources(-1);
            fail("Negative numbers are not allowed");
        } catch (IllegalArgumentException ex) {
            assertEquals(workingNumber, completeGatewayImpl.getDesiredAvailableResources());
        }
    }

    public class CompleteGatewayImpl extends CompleteGateway {

        public void send(Message msg) {
        }
    }
    
}

package resourcescheduler;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.MessageFactory;
import resourcescheduler.utils.GeneralUtilities;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceSchedulerTest {

    private ResourceScheduler resourceScheduler;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.resourceScheduler = new ResourceScheduler();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCanConfigureResourcesQuantity() {
        System.out.println("canConfigureResourcesQuantity");

        assertEquals(0, resourceScheduler.getResourcesQuantity());

        int exampleResourcesNumber = 2;

        resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());

        exampleResourcesNumber = 3;
        resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());

        /*Random numbers test*/
        int[] exampleResourcesNumbers = new int[10];
        for (int i = 0; i < exampleResourcesNumbers.length; i++) {
            exampleResourcesNumber = GeneralUtilities.generateRandomNumber(0, Integer.MAX_VALUE);
            resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
            assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());
        }
    }

    @Test
    public void testCanReceiveAndQueueMessages() {
        System.out.println("canReceiveAndQueueMessages");
        int queuedMessagesCount;
        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = resourceScheduler.getQueuedMessagesCount();
        assertEquals(1, queuedMessagesCount);

        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = resourceScheduler.getQueuedMessagesCount();
        assertEquals(2, queuedMessagesCount);

        int numberOfNewDummyMessagesToSend = 8;
        for (int i = 0; i < numberOfNewDummyMessagesToSend; i++) {
            resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
            queuedMessagesCount++;
            assertEquals(queuedMessagesCount, resourceScheduler.getQueuedMessagesCount());
        }
    }
}

package resourcescheduler;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.MessageFactory;
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
        Assert.assertEquals(0, resourceScheduler.getResourcesQuantity());

        int exampleResourcesNumber = 2;

        resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        Assert.assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());

        exampleResourcesNumber = 3;
        resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        Assert.assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());

        /*Random numbers test*/
        int[] exampleResourcesNumbers = new int[10];
        for (int i = 0; i < exampleResourcesNumbers.length; i++) {
            exampleResourcesNumber = GeneralUtilities.generateRandomNumber(0, Integer.MAX_VALUE);
            resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
            Assert.assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());
        }
    }

    @Test
    public void testCanReceiveMessages() {

        int queuedMessagesCount;
        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = resourceScheduler.getQueuedMessagesCount();
        Assert.assertEquals(1, queuedMessagesCount);

        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = resourceScheduler.getQueuedMessagesCount();
        Assert.assertEquals(2, queuedMessagesCount);

        int numberOfNewDummyMessagesToSend = 8;
        for (int i = 0; i < numberOfNewDummyMessagesToSend; i++) {
            resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
            queuedMessagesCount++;
            Assert.assertEquals(queuedMessagesCount, resourceScheduler.getQueuedMessagesCount());
        }
    }
}

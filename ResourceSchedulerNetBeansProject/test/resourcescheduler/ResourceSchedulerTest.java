package resourcescheduler;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.DummyGatewayFactory;
import resourcescheduler.model.message.MessageFactory;
import resourcescheduler.utils.GeneralUtilities;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceSchedulerTest {

    private ResourceScheduler dummyResourceScheduler;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.dummyResourceScheduler = new ResourceScheduler(new DummyGatewayFactory());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCanConfigureResourcesQuantity() {
        System.out.println("canConfigureResourcesQuantity");

        assertEquals(0, dummyResourceScheduler.getResourcesQuantity());

        int exampleResourcesNumber = 2;

        dummyResourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, dummyResourceScheduler.getResourcesQuantity());

        exampleResourcesNumber = 3;
        dummyResourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, dummyResourceScheduler.getResourcesQuantity());

        /*Random numbers test*/
        int[] exampleResourcesNumbers = new int[10];
        for (int i = 0; i < exampleResourcesNumbers.length; i++) {
            exampleResourcesNumber = GeneralUtilities.generateRandomNumber(0, Integer.MAX_VALUE);
            dummyResourceScheduler.setResourcesQuantity(exampleResourcesNumber);
            assertEquals(exampleResourcesNumber, dummyResourceScheduler.getResourcesQuantity());
        }
    }

    @Test
    public void testCanReceiveAndQueueMessages() {
        System.out.println("canReceiveAndQueueMessages");

        int queuedMessagesCount;
        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = dummyResourceScheduler.getQueuedMessagesCount();
        assertEquals(1, queuedMessagesCount);

        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        queuedMessagesCount = dummyResourceScheduler.getQueuedMessagesCount();
        assertEquals(2, queuedMessagesCount);

        final int numberOfNewDummyMessagesToSend = 8;
        for (int i = 0; i < numberOfNewDummyMessagesToSend; i++) {
            dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
            queuedMessagesCount++;
            assertEquals(queuedMessagesCount, dummyResourceScheduler.getQueuedMessagesCount());
        }
    }

    @Test
    public void testCanSendMessagesWhenResourcesAvailable() {
        System.out.println("canSendMessagesWhenResourcesAvailable");

        for (int i = 0; i < 10; i++) {
            dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        }
        
        assertEquals(10, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(1);
        assertEquals(9, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(3);/*We get two more resources*/
        assertEquals(7, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(1);/*No more resources added, so same quantity of queuede msgs*/
        assertEquals(7, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(6);/*5 new resources*/
        assertEquals(2, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(2);
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(0);
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setResourcesQuantity(3);/*3 resources free*/

        for (int i = 0; i < 3; i++) {
            dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
            assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        }

        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(1, dummyResourceScheduler.getQueuedMessagesCount());/*1 msg queued*/
        
        dummyResourceScheduler.setResourcesQuantity(30000);
        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());/*1 msg queued*/

    }
}

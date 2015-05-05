    package resourcescheduler;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.fakeimplementations.DummyGateway;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.message.MessageFactory;
import resourcescheduler.utils.GeneralUtilities;
import static resourcescheduler.utils.GeneralUtilities.generateRandomNumber;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceSchedulerTest {

    private static Gateway dummyGateway;

    private ResourceScheduler dummyResourceScheduler;

    @BeforeClass
    public static void setUpClass() {
        dummyGateway = new DummyGateway().createGateway();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.dummyResourceScheduler = new ResourceScheduler(dummyGateway);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCanConfigureDesiredResourcesQuantity() {
        System.out.println("testCanConfigureDesiredResourcesQuantity");

        assertEquals(0, dummyResourceScheduler.getDesiredResourcesQuantity());

        int exampleResourcesNumber = 2;

        dummyResourceScheduler.setDesiredResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, dummyResourceScheduler.getDesiredResourcesQuantity());

        exampleResourcesNumber = 3;
        dummyResourceScheduler.setDesiredResourcesQuantity(exampleResourcesNumber);
        assertEquals(exampleResourcesNumber, dummyResourceScheduler.getDesiredResourcesQuantity());

        /*Random numbers test*/
        int[] exampleResourcesNumbers = new int[10];
        for (int i = 0; i < exampleResourcesNumbers.length; i++) {
            
            exampleResourcesNumber = generateRandomNumber(0, Integer.MAX_VALUE);
            
            dummyResourceScheduler.setDesiredResourcesQuantity(exampleResourcesNumber);
            assertEquals(exampleResourcesNumber, dummyResourceScheduler.getDesiredResourcesQuantity());
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

        dummyResourceScheduler.setDesiredResourcesQuantity(1);
        assertEquals(9, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(3);/*We get two more resources*/

        assertEquals(7, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(1);/*No more resources added, so same quantity of queuede msgs*/

        assertEquals(7, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(6);/*5 new resources*/

        assertEquals(2, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(2);
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(0);
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        dummyResourceScheduler.setDesiredResourcesQuantity(3);/*3 resources free*/

        for (int i = 0; i < 3; i++) {
            dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
            assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());

        }

        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(1, dummyResourceScheduler.getQueuedMessagesCount());/*1 msg queued*/

        dummyResourceScheduler.setDesiredResourcesQuantity(30000);
        dummyResourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(0, dummyResourceScheduler.getQueuedMessagesCount());/*1 msg queued*/

    }
}

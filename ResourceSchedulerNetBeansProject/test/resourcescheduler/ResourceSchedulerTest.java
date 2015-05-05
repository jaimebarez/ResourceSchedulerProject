package resourcescheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.CompleteMsgNotifyingGateway;
import resourcescheduler.model.gateway.fakeimplementations.DummyGateway;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.gateway.fakeimplementations.ManualGateway;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.MessageFactory;
import static resourcescheduler.utils.GeneralUtilities.generateRandomNumber;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceSchedulerTest {

    private static CompleteMsgNotifyingGateway dummyGateway;

    private ResourceScheduler dummyResourceScheduler;

    @BeforeClass
    public static void setUpClass() {
        dummyGateway = new DummyGateway();
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
        //0 is default value
        assertEquals(0, dummyResourceScheduler.getDesiredResourcesQuantity());

        //It can work with many numbers
        final int[] exampleResourcesNumbers = new int[]{1, 2, 10, 5, 0, 100, 0};
        for (int exampleResourcesNumber : exampleResourcesNumbers) {
            dummyResourceScheduler.setDesiredResourcesQuantity(exampleResourcesNumber);
            assertEquals(exampleResourcesNumber, dummyResourceScheduler.getDesiredResourcesQuantity());
        }

        //Random numbers test
        int[] exampleRandomResourcesNumbers = new int[10];
        for (int i = 0; i < exampleRandomResourcesNumbers.length; i++) {

            int randomNumber = generateRandomNumber(0, Integer.MAX_VALUE);
            dummyResourceScheduler.setDesiredResourcesQuantity(randomNumber);
            assertEquals(randomNumber, dummyResourceScheduler.getDesiredResourcesQuantity());
        }

        //Negative numbers test
        int workingNumber = 350;
        dummyResourceScheduler.setDesiredResourcesQuantity(workingNumber);
        assertEquals(workingNumber, dummyResourceScheduler.getDesiredResourcesQuantity());
        try {
            dummyResourceScheduler.setDesiredResourcesQuantity(-1);
            fail("Negative numbers are not allowed");
        } catch (IllegalArgumentException ex) {
            assertEquals(workingNumber, dummyResourceScheduler.getDesiredResourcesQuantity());
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
    public void testCanSendMessagesWhenProvidingAvailableResources() {
        System.out.println("testCanSendMessagesWhenProvidingAvailableResources");

        final AtomicInteger sentMessages = new AtomicInteger(0);
        CompleteMsgNotifyingGateway gateway = new CompleteMsgNotifyingGateway() {

            @Override
            public void send(Message msg) {
                sentMessages.incrementAndGet();
            }
        };
        ResourceScheduler resourceScheduler = new ResourceScheduler(gateway);

        for (int i = 0; i < 10; i++) {

            resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        }

        //Zero sent messages
        assertEquals(0, sentMessages.get());
        //One resource, one sent
        resourceScheduler.setDesiredResourcesQuantity(1);
        assertEquals(1, sentMessages.get());
        //+2 resources, +2 sent
        resourceScheduler.setDesiredResourcesQuantity(3);
        assertEquals(3, sentMessages.get());

        //Less resources, no message sent
        resourceScheduler.setDesiredResourcesQuantity(1);
        assertEquals(3, sentMessages.get());

        /*We set one more resource, but messages are still processing, 
         so the same number of messages sent*/
        resourceScheduler.setDesiredResourcesQuantity(2);
        assertEquals(3, sentMessages.get());

        //The same
        resourceScheduler.setDesiredResourcesQuantity(7);
        assertEquals(7, sentMessages.get());

        /*Plenty resources, but we can only send 10 messages*/
        resourceScheduler.setDesiredResourcesQuantity(99999);
        assertEquals(10, sentMessages.get());

        resourceScheduler.setDesiredResourcesQuantity(0);
        assertEquals(10, sentMessages.get());

        //We have 12 resources, so we can send new Messages until no more resources left
        resourceScheduler.setDesiredResourcesQuantity(12);
        assertEquals(10, sentMessages.get());

        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(11, sentMessages.get());
        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(12, sentMessages.get());
        //No more resources
        resourceScheduler.reveiveMessage(MessageFactory.createDummyMessage());
        assertEquals(12, sentMessages.get());
    }

    @Test
    public void testCanSendMessagesWhenResourcesBecomeAvailable() {

        final AtomicInteger sentMessages = new AtomicInteger(0);
        ManualGateway manualGateway = new ManualGateway() {
            int i = 0;

            @Override
            public void send(Message msg) {
                super.send(msg);
                System.out.println(i++);
                sentMessages.incrementAndGet();
            }
        };

        ResourceScheduler resourceScheduler = new ResourceScheduler(manualGateway);
        resourceScheduler.setDesiredResourcesQuantity(10);

        final List<Message> processingMessages = new LinkedList<>();
        for (int i = 0; i < 15; i++) {
            Message dummyMessage = MessageFactory.createDummyMessage();
            resourceScheduler.reveiveMessage(dummyMessage);
            if (i < 10) {
                processingMessages.add(dummyMessage);
            }
        }
        //5 last messages queued
        assertEquals(10, sentMessages.get());
        assertEquals(5, resourceScheduler.getQueuedMessagesCount());
        Message remove = processingMessages.remove(0);
        
        System.out.println("--------------");
        assertTrue(manualGateway.processSentMessage(remove));
        System.out.println("--------------");
        assertEquals(4, resourceScheduler.getQueuedMessagesCount());
        System.out.println("aaaaaaaaaaaaaaaaaa");

    }
}

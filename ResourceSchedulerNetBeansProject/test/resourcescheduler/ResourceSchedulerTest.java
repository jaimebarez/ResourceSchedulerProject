package resourcescheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.gateway.fakeimplementations.DummyGateway;
import resourcescheduler.model.gateway.fakeimplementations.ManualGateway;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceSchedulerTest {

    private static CompleteGateway dummyGateway;

    private ResourceScheduler dummyRSched;

    @BeforeClass
    public static void setUpClass() {
        dummyGateway = new DummyGateway();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.dummyRSched = new ResourceScheduler(dummyGateway);
    }

    @After
    public void tearDown() {
    }

//    @Test
//    public void testCanReceiveAndQueueMessages() {
//        System.out.println("canReceiveAndQueueMessages");
//
//        int queuedMessagesCount;
//        dummyRSched.reveiveMessage(new DummyMessage());
//        queuedMessagesCount = dummyRSched.getQueuedMessagesCount();
//        assertEquals(1, queuedMessagesCount);
//
//        dummyRSched.reveiveMessage(new DummyMessage());
//        queuedMessagesCount = dummyRSched.getQueuedMessagesCount();
//        assertEquals(2, queuedMessagesCount);
//
//        final int numberOfNewDummyMessagesToSend = 8;
//        for (int i = 0; i < numberOfNewDummyMessagesToSend; i++) {
//            dummyRSched.reveiveMessage(new DummyMessage());
//            queuedMessagesCount++;
//            assertEquals(queuedMessagesCount, dummyRSched.getQueuedMessagesCount());
//        }
//    }
    @Test
    public void testCanSendMessagesWhenProvidingAvailableResources() {
        System.out.println("testCanSendMessagesWhenProvidingAvailableResources");

        final AtomicInteger sentMessages = new AtomicInteger(0);
        CompleteGateway gateway = new CompleteGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);
                sentMessages.incrementAndGet();
            }
        };
        ResourceScheduler resourceScheduler = new ResourceScheduler(gateway);

        for (int i = 0; i < 10; i++) {

            resourceScheduler.reveiveMessage(new DummyMessage());
        }

        //Zero sent messages
        assertEquals(0, sentMessages.get());
        //One resource, one sent

        gateway.setDesiredAvailableResources(1);
        assertEquals(1, sentMessages.get());

        //+2 resources, +2 sent
        gateway.setDesiredAvailableResources(3);
        assertEquals(3, sentMessages.get());

        //Less resources, no message sent
        gateway.setDesiredAvailableResources(1);
        assertEquals(3, sentMessages.get());

        /*We set one more resource, but messages are still processing, 
         so the same number of messages sent*/
        gateway.setDesiredAvailableResources(2);
        assertEquals(3, sentMessages.get());

        //The same
        gateway.setDesiredAvailableResources(7);
        assertEquals(7, sentMessages.get());

        /*Plenty resources, but we can only send 10 messages*/
        gateway.setDesiredAvailableResources(999999);
        assertEquals(10, sentMessages.get());

        gateway.setDesiredAvailableResources(0);
        assertEquals(10, sentMessages.get());

        //We have 12 resources, so we can send new Messages until no more resources left
        gateway.setDesiredAvailableResources(12);
        assertEquals(10, sentMessages.get());

        resourceScheduler.reveiveMessage(new DummyMessage());
        assertEquals(11, sentMessages.get());
        resourceScheduler.reveiveMessage(new DummyMessage());
        assertEquals(12, sentMessages.get());
        assertEquals(0, resourceScheduler.getQueuedMessagesCount());

        //No more resources
        resourceScheduler.reveiveMessage(new DummyMessage());
        assertEquals(12, sentMessages.get());
        assertEquals(1, resourceScheduler.getQueuedMessagesCount());
    }

    @Test
    public void testCanSendMessagesWhenResourcesBecomeAvailable() {

        final AtomicInteger sentMessages = new AtomicInteger(0);

        ManualGateway manualGateway = new ManualGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);

                sentMessages.incrementAndGet();
            }
        };

        ResourceScheduler resourceScheduler = new ResourceScheduler(manualGateway);
        manualGateway.setDesiredAvailableResources(10);

        final List<Message> processingMessages = new LinkedList<>();
        for (int i = 0; i < 15; i++) {
            Message dummyMessage = new DummyMessage();
            resourceScheduler.reveiveMessage(dummyMessage);
            if (i < 10) {
                processingMessages.add(dummyMessage);
            }
        }
        //5 last messages queued
        assertEquals(10, sentMessages.get());
        assertEquals(5, resourceScheduler.getQueuedMessagesCount());

        for (int i = 4; i >= 0; i--) {
            Message remove = processingMessages.remove(0);
            assertTrue(manualGateway.processSentMessage(remove));
            assertEquals(i, resourceScheduler.getQueuedMessagesCount());
        }
        assertEquals(15, sentMessages.get());
        //No efffect
        manualGateway.processSentMessage(new DummyMessage());
        assertEquals(15, sentMessages.get());

    }
}

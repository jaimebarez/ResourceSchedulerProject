package resourcescheduler.resourcescheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.gateway.fakeimplementations.DifferentSpeedsProcessingGateway;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.gateway.fakeimplementations.ManualGateway;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.Message;
import resourcescheduler.resourcescheduler.exceptions.MessageReceivementException;
import static resourcescheduler.utils.GeneralUtilities.generateRandomNumber;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class ResourceSchedulerTest {

    @Test
    public void testCanReceiveAndQueueMessages() throws MessageReceivementException {
        System.out.println("testCanReceiveAndQueueMessages");

        ResourceScheduler rSched = new ResourceScheduler(new InstantProcessingGateway());

        int queuedMessagesCount;

        rSched.receiveMessage(new DummyMessage());
        queuedMessagesCount = rSched.getQueuedMessagesCount();
        assertEquals(1, queuedMessagesCount);

        rSched.receiveMessage(new DummyMessage());
        queuedMessagesCount = rSched.getQueuedMessagesCount();
        assertEquals(2, queuedMessagesCount);

        final int numberOfNewDummyMessagesToSend = 8;
        for (int i = 0; i < numberOfNewDummyMessagesToSend; i++) {
            rSched.receiveMessage(new DummyMessage());

            assertEquals(++queuedMessagesCount, rSched.getQueuedMessagesCount());
        }
    }

    @Test
    public void testCanSendMessagesWhenProvidingAvailableResources() throws MessageReceivementException {
        System.out.println("testCanSendMessagesWhenProvidingAvailableResources");

        /*Don't mind atomic or not, just want a final object to place my values*/
        final AtomicInteger sentMessages = new AtomicInteger(0);

        NotifyingGatewayImpl gateway = new ManualGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);
                sentMessages.incrementAndGet();
            }
        };

        ResourceScheduler resourceScheduler = new ResourceScheduler(gateway);
        int maxAvlbResources = 10;

        for (int i = 0; i < maxAvlbResources; i++) {

            resourceScheduler.receiveMessage(new DummyMessage());
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

        //More available resources, more messages sent
        gateway.setDesiredAvailableResources(7);
        assertEquals(7, sentMessages.get());

        /*Plenty resources, but we can only send 10 messages*/
        gateway.setDesiredAvailableResources(999999);
        assertEquals(maxAvlbResources, sentMessages.get());

        gateway.setDesiredAvailableResources(0);
        assertEquals(maxAvlbResources, sentMessages.get());

        //We have 12 resources, so we can send new Messages until no more resources left
        gateway.setDesiredAvailableResources(12);
        assertEquals(maxAvlbResources, sentMessages.get());

        resourceScheduler.receiveMessage(new DummyMessage());
        assertEquals(++maxAvlbResources, sentMessages.get());
        resourceScheduler.receiveMessage(new DummyMessage());
        assertEquals(++maxAvlbResources, sentMessages.get());
        assertEquals(0, resourceScheduler.getQueuedMessagesCount());

        //No more resources, no more sent
        resourceScheduler.receiveMessage(new DummyMessage());
        assertEquals(maxAvlbResources, sentMessages.get());
        assertEquals(1, resourceScheduler.getQueuedMessagesCount());
    }

    @Test
    public void testCanSendMessagesWhenResourcesBecomeAvailable() throws MessageReceivementException {
        System.out.println("testCanSendMessagesWhenResourcesBecomeAvailable");

        final AtomicInteger sentMessages = new AtomicInteger(0);

        ManualGateway manualGateway = new ManualGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);

                sentMessages.incrementAndGet();
            }
        };
        int availRsrces = 10;
        int queuedResources = 5;
        int msgsToSend = availRsrces + queuedResources;

        ResourceScheduler resourceScheduler = new ResourceScheduler(manualGateway);
        manualGateway.setDesiredAvailableResources(availRsrces);

        //Start the scenario
        final List<Message> processingMessages = new LinkedList<>();

        for (int i = 0; i < msgsToSend; i++) {
            Message dummyMessage = new DummyMessage();
            resourceScheduler.receiveMessage(dummyMessage);
            if (i < availRsrces) {
                processingMessages.add(dummyMessage);
            }
        }
        //5 last messages queued
        assertEquals(availRsrces, sentMessages.get());
        assertEquals(queuedResources, resourceScheduler.getQueuedMessagesCount());

        for (int i = queuedResources - 1; i >= 0; i--) {
            Message remove = processingMessages.remove(0);
            assertTrue(manualGateway.processSentMessage(remove));
            assertEquals(i, resourceScheduler.getQueuedMessagesCount());
        }
        assertEquals(msgsToSend, sentMessages.get());
        //Queued
        manualGateway.processSentMessage(new DummyMessage());
        assertEquals(msgsToSend, sentMessages.get());
    }

    @Test
    public void testMessagesNotGuaranteedToBeDeliveredInTheirGroups() throws InterruptedException, MessageReceivementException {
        System.out.println("testMessagesNotGuaranteedToBeDeliveredInTheirGroups");

        DifferentSpeedsProcessingGateway gw = new DifferentSpeedsProcessingGateway();

        ResourceScheduler rSch = new ResourceScheduler(gw);
        gw.setDesiredAvailableResources(1);
        final Long[] groups = new Long[]{1L, 2L, 1L, 2L, 1L, 2L, 4L,
            6L, 1L, 2L, 1L, 2L, 1L, 2L, 1L, 2L, 1L, 3L, 4L};

        final Thread currentThread = Thread.currentThread();
        int processingMillis = 200;

        int maxCheckTimes = 3;
        boolean checkAgain = true;
        for (int i = 1; checkAgain && i <= maxCheckTimes; i++) {
            final List<Long> receivedGroupsOrder = new ArrayList<>();

            final AtomicInteger counter = new AtomicInteger(1);
            final AtomicBoolean finishedExtremelyFast = new AtomicBoolean(false);

            for (long group : groups) {
                gw.setNextProcessingMillis(generateRandomNumber(0, processingMillis));
                rSch.receiveMessage(new Message(group) {

                    @Override
                    public void completed() {
                        long groupId = this.getGroupId();
                        synchronized (currentThread) {
                            receivedGroupsOrder.add(groupId);
                        }
                        if (counter.getAndIncrement() == groups.length) {
                            synchronized (currentThread) {
                                finishedExtremelyFast.set(true);
                                currentThread.notify();
                            }
                        }
                    }
                });
            }

            synchronized (currentThread) {
                /*If processing millis are too fast, we could call wait after 
                 every message called notify, and we would wait forever*/
                if (!finishedExtremelyFast.get()) {

                    currentThread.wait();
                }
            }
            final ArrayList<Long> receivedGroupsOrderOrderedList
                    = new ArrayList<Long>(receivedGroupsOrder) {
                        {
                            Collections.sort(this);
                        }
                    };

            boolean equals
                    = Arrays.equals(receivedGroupsOrder.toArray(), receivedGroupsOrderOrderedList.toArray());
            /*There is unlikely to be completed in the order of entry, but that case is there, so we check several times.*/
            if (equals && i == maxCheckTimes) {
                fail("Always recovered at the same order");
            } else if (!equals) {

                checkAgain = false;
            }
        }
    }

    @Test
    public void testGroupsReceiveOrderAndNoInterleaving() throws MessageReceivementException {
        System.out.println("testGroupsReceiveOrderAndNoInterleaving");

        final Long[][] unorderedGroups = new Long[][]{{2L, 4L, 1L, 1L, 2L, 4L}, {1L, 4L, 5L, 4L, 6L, 5L}};
        final Long[][] orderedGroups = new Long[][]{{2L, 2L, 4L, 4L, 1L, 1L}, {4L, 4L, 1L, 5L, 5L, 6L}};

        final List<Long> sentGroupsOrder = new ArrayList<>();
        NotifyingGatewayImpl gw = new InstantProcessingGateway();
        ResourceScheduler rSch = new ResourceScheduler(gw) {

            @Override
            protected void sendMessageToWateway(Message nextUnsentMsg) {
                super.sendMessageToWateway(nextUnsentMsg);
                long groupId = ((Message) nextUnsentMsg).getGroupId();

                sentGroupsOrder.add(groupId);
            }
        };

        for (int i = 0; i < orderedGroups.length; i++) {
            sentGroupsOrder.clear();
            gw.setDesiredAvailableResources(0);//All queued

            for (Long group : unorderedGroups[i]) {
                Message groupingMessage = new DummyMessage(group);

                rSch.receiveMessage(groupingMessage);
            }
            gw.setDesiredAvailableResources(1);//Processing time!

            assertArrayEquals(orderedGroups[i], sentGroupsOrder.toArray());

            //Reset scenario
            gw.setDesiredAvailableResources(0);
        }
    }

    @Test
    public void testMessagesAreForwardedToGatewayWhenAvailableResources() throws MessageReceivementException {
        System.out.println("testMessagesAreForwardedToGatewayWhenAvailableResources");

        final List<Message> msgsReceivedByGateway = new ArrayList<>();
        ManualGateway gw = new ManualGateway() {

            @Override
            public void send(Message msg) {
                msgsReceivedByGateway.add(msg);
                super.send(msg);
            }
        };
        ResourceScheduler rSched = new ResourceScheduler(gw);

        int[] numResources = {1, 2};

        for (int n : numResources) {
            msgsReceivedByGateway.clear();
            gw.processAllQueued();

            gw.setDesiredAvailableResources(n);
            for (int i = 0; i <= n; i++) {
                rSched.receiveMessage(new DummyMessage());
            }

            assertEquals(msgsReceivedByGateway.size(), n);
            assertEquals(rSched.getQueuedMessagesCount(), 1);
            gw.setDesiredAvailableResources(Integer.MAX_VALUE);
        }
    }

    @Test
    public void testQueuing() throws MessageReceivementException {
        System.out.println("testQueuing");

        final List<Message> msgsReceivedByGateway = new ArrayList<>();
        ManualGateway gw = new ManualGateway() {

            @Override
            public void send(Message msg) {
                msgsReceivedByGateway.add(msg);
                super.send(msg);
            }
        };
        ResourceScheduler rSched = new ResourceScheduler(gw);
        gw.setDesiredAvailableResources(0);
        Message firstMessage = new DummyMessage();
        rSched.receiveMessage(firstMessage);

        assertEquals(0, msgsReceivedByGateway.size());

        rSched.receiveMessage(new DummyMessage());
        gw.setDesiredAvailableResources(1);

        assertEquals(1, msgsReceivedByGateway.size());
        assertEquals(firstMessage, msgsReceivedByGateway.get(0));
    }
}

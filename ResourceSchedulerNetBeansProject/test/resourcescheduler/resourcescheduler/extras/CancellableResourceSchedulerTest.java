package resourcescheduler.resourcescheduler.extras;

import resourcescheduler.resourcescheduler.exceptions.MessageReceivementException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.Message;
import resourcescheduler.resourcescheduler.ResourceScheduler;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class CancellableResourceSchedulerTest {

    @Test
    public void testCanCancelAGroupOfMessages() throws MessageReceivementException {
        System.out.println("testCanCancelAGroupOfMessages");

        final Set<Long> receivedGroups = new HashSet<>();
        InstantProcessingGateway gateway = new InstantProcessingGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);
                if (msg instanceof Message) {
                    receivedGroups.add(((Message) msg).getGroupId());
                }
            }
        };
        ResourceScheduler cRsched = new ResourceScheduler(gateway);
        gateway.setDesiredAvailableResources(1);

        final List<Long> groupsToSend = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new DummyMessage(group));
        }
        assertTrue(receivedGroups.containsAll(groupsToSend));

        receivedGroups.clear();
        cRsched.addCancellableGroup(4);

        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new DummyMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));

        //Cancelable group that does not exist. Nothing bad happens
        receivedGroups.clear();
        cRsched.addCancellableGroup(99);
        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new DummyMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));//Was previously canceled

        receivedGroups.clear();
        cRsched.addCancellableGroup(3);
        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new DummyMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));
        assertFalse(receivedGroups.contains(3L));
    }
}

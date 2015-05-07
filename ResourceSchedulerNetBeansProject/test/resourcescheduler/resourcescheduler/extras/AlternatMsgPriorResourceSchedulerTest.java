package resourcescheduler.resourcescheduler.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.Message;
import resourcescheduler.resourcescheduler.MessagePriorisator;
import resourcescheduler.resourcescheduler.ResourceScheduler;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class AlternatMsgPriorResourceSchedulerTest {

    /**
     * Test of setMessagePriorisator method, of class
     * AlternatMsgPriorResourceScheduler.
     */
    @Test
    public void testAlternativeMessagePriorisationAndEasyOriginalAlgorithmRecover() throws MessageReceivementException {

        final List<Long> receivedIds = new ArrayList<>();

        NotifyingGatewayImpl gw = new InstantProcessingGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);
                receivedIds.add(msg.getGroupId());
            }
        };
        gw.setDesiredAvailableResources(0);

        ResourceScheduler rSched
                = new ResourceScheduler(gw);

        final MessagePriorisator originalMessagePriorisator = rSched.getMessagePriorisator();

        rSched.setMessagePriorisator(new AscendingGroupIdMessagePriorisator());

        List<Long> ids = Arrays.asList(5L, 3L, 5L, 1L, Long.MIN_VALUE, 0L, 9L, Long.MAX_VALUE, 14L);
        for (Long id : ids) {
            rSched.receiveMessage(new DummyMessage(id));
        }
        gw.setDesiredAvailableResources(1);
        List<Long> orderedIds = new ArrayList<Long>(ids) {
            {
                Collections.sort(this);
            }
        };

        assertArrayEquals(orderedIds.toArray(), receivedIds.toArray());

        //Recovering original algorithm:
        rSched.setMessagePriorisator(originalMessagePriorisator);
        receivedIds.clear();
        gw.setDesiredAvailableResources(0);
        
        final Long[] groupEnterOrder =new Long[]{5L, 5L, 3L, 1L, Long.MIN_VALUE, 0L, 9L, Long.MAX_VALUE, 14L};
        for (Long id : ids) {
            rSched.receiveMessage(new DummyMessage(id));
        }
        gw.setDesiredAvailableResources(1);
        assertArrayEquals(groupEnterOrder, receivedIds.toArray());

    }

    private class AscendingGroupIdMessagePriorisator implements MessagePriorisator {

        @Override
        public void register(Message msg) {
        }

        @Override
        public void unregister(Message msg) {
        }

        @Override
        public Comparator<Message> getComparator() {
            return new Comparator<Message>() {

                @Override
                public int compare(Message o1, Message o2) {
                    return Long.compare(o2.getGroupId(), o1.getGroupId());
                }
            };
        }

    }

}

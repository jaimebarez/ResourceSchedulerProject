package resourcescheduler.resourcescheduler.extras;

import resourcescheduler.resourcescheduler.exceptions.MessageReceivementException;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.TerminationMessage;
import resourcescheduler.resourcescheduler.ResourceScheduler;
import resourcescheduler.resourcescheduler.exceptions.PreviousTermMsgReceivedException;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class GroupTerminationAbleResourceSchedulerTest {

    @Test
    public void testTerminationMessages() throws MessageReceivementException {
        System.out.println("testTerminationMessages");

        NotifyingGatewayImpl gw = new InstantProcessingGateway();

        ResourceScheduler rSched = new ResourceScheduler(gw);

        rSched.receiveMessage(new DummyMessage(1));
        rSched.receiveMessage(new DummyMessage(3));
        rSched.receiveMessage(new DummyMessage(2));
        rSched.receiveMessage(new TerminationMessageImpl(1));
        try {
            rSched.receiveMessage(new DummyMessage(1));
            fail("Did not raise an error");
        } catch (PreviousTermMsgReceivedException ex) {

        }
        try {
            rSched.receiveMessage(new TerminationMessageImpl(1));
            fail("Did not raise an error");
        } catch (PreviousTermMsgReceivedException ex) {
            //Exception OK
        }
        rSched.receiveMessage(new TerminationMessageImpl(2));
        try {
            rSched.receiveMessage(new DummyMessage(2));
            fail("Did not raise an error");
        } catch (PreviousTermMsgReceivedException ex) {
            //Exception OK
        }
    }

    private class TerminationMessageImpl extends TerminationMessage {

        public TerminationMessageImpl(long groupId) {
            super(groupId);
        }

        @Override
        public void completed() {
        }

    }

}

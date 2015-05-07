package resourcescheduler.resourcescheduler.extras;

import java.util.ArrayList;
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
import resourcescheduler.model.message.TerminationMessage;
import resourcescheduler.resourcescheduler.ResourceScheduler;

/**
 *
 * @author jaimebarez
 */
public class GroupTerminationAbleResourceSchedulerTest {

    /**
     * Test of receiveMessage method, of class
     * GroupTerminationAbleResourceScheduler.
     */
    @Test
    public void testTerminationMessages() throws Exception {
        System.out.println("receiveMessage");

        NotifyingGatewayImpl gw = new InstantProcessingGateway();

        ResourceScheduler rSched = new ResourceScheduler(gw);

        rSched.receiveMessage(new DummyMessage(1));
        rSched.receiveMessage(new DummyMessage(3));
        rSched.receiveMessage(new DummyMessage(2));
        rSched.receiveMessage(new TerminationMessageImpl(1));
        try {
            rSched.receiveMessage(new DummyMessage(1));
            fail("Did not raise an error");
        } catch (MessageReceivementException ex) {

        }
        try {
            rSched.receiveMessage(new TerminationMessageImpl(1));
            fail("Did not raise an error");
        } catch (MessageReceivementException ex) {

        }
        rSched.receiveMessage(new TerminationMessageImpl(2));
        try {
            rSched.receiveMessage(new DummyMessage(2));
            fail("Did not raise an error");
        } catch (MessageReceivementException ex) {

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

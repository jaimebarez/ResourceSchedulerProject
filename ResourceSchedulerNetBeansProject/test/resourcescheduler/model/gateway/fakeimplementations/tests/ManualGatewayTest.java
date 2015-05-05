package resourcescheduler.model.gateway.fakeimplementations.tests;

import resourcescheduler.model.gateway.fakeimplementations.ManualGateway;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.MessageFactory;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ManualGatewayTest {

    private ManualGateway manualGateway;
    private Message dummyMessage;

    @Before
    public void setUp() {
        this.manualGateway = new ManualGateway();
        this.dummyMessage = MessageFactory.createDummyMessage();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSend() {
        System.out.println("testSend");

        manualGateway.send(dummyMessage);
        assertTrue(manualGateway.processSentMessage(dummyMessage));
        assertFalse(manualGateway.processSentMessage(dummyMessage));

    }

    /**
     * Test of processSentMessage method, of class ManualGateway.
     */
    @Test
    public void testProcessSentMessage() {
        System.out.println("testProcessSentMessage");

        assertFalse(manualGateway.processSentMessage(MessageFactory.createDummyMessage()));
        manualGateway.send(dummyMessage);
        assertTrue(manualGateway.processSentMessage(dummyMessage));
        assertFalse(manualGateway.processSentMessage(dummyMessage));

        final AtomicBoolean reallyProcessed = new AtomicBoolean(false);
        Message msg = new Message() {

            @Override
            public void completed() {
                reallyProcessed.set(true);
            }
        };
        manualGateway.send(msg);
        assertFalse(reallyProcessed.get());
        manualGateway.processSentMessage(msg);
        assertTrue(reallyProcessed.get());
    }
}

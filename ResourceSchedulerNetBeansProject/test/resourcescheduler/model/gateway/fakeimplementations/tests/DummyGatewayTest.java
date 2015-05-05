package resourcescheduler.model.gateway.fakeimplementations.tests;

import resourcescheduler.model.gateway.fakeimplementations.DummyGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.DummyMessage;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class DummyGatewayTest {

    private DummyGateway dummyGateway;
    private Message dummyMessage;

    @Before
    public void setUp() {
        Message m = null;

        this.dummyGateway = new DummyGateway();
        this.dummyMessage = new DummyMessage();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSend() {
        System.out.println("testSend");

        dummyGateway.send(dummyMessage);
    }

}

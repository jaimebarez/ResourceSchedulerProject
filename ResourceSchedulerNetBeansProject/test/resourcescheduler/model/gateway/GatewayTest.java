package resourcescheduler.model.gateway;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.DummyMessage;

/**
 *
 * @author jaime.barez.lobato
 */
public class GatewayTest {

    private GatewayImpl gatewayImpl;

    @Before
    public void setUp() {
        gatewayImpl = new GatewayImpl();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSend() {
        System.out.println("testSend");
        Message msg = new DummyMessage();

        gatewayImpl.send(msg);
    }

    public class GatewayImpl implements Gateway {

        public void send(Message msg) {
        }
    }

}

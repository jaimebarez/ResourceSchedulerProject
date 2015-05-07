package resourcescheduler.model.gateway;

import org.junit.After;
import org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.DummyMessage;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
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
        assertNotNull(gatewayImpl);
        Message msg = new DummyMessage();

        gatewayImpl.send(msg);
        //No errors? All OK
    }

    public class GatewayImpl implements Gateway {

        public void send(Message msg) {
        }
    }

}

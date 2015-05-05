/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcescheduler.model.gateway;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.MessageFactory;

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
        Message msg = MessageFactory.createDummyMessage();

        gatewayImpl.send(msg);
    }

    public class GatewayImpl implements Gateway {

        public void send(Message msg) {
        }
    }

}

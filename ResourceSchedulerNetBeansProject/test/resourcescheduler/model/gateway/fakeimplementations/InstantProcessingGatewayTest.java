package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.DummyMessage;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class InstantProcessingGatewayTest {

    private InstantProcessingGateway instantProcessingGateway;

    @Before
    public void setUp() {
        this.instantProcessingGateway = new InstantProcessingGateway();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSend() {
        System.out.println("testSend");

        final AtomicBoolean reallyProcessed = new AtomicBoolean(false);
        final Object lock = new Object();
        Message msg = new DummyMessage() {

            @Override
            public void completed() {
                synchronized (lock) {//LOCK
                    reallyProcessed.set(true);
                }
            }
        };

        assertFalse(reallyProcessed.get());

        synchronized (lock) {//LOCK
            instantProcessingGateway.send(msg);

            assertTrue(reallyProcessed.get());
        }
        /*Thaks to the lock we can ensure that it is instant, no threads*/
    }
}

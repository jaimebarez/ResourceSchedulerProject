package resourcescheduler.model.gateway.fakeimplementations.tests;

import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
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
        Message msg = new Message() {

            @Override
            public void completed() {
                reallyProcessed.set(true);
            }
        };

        assertFalse(reallyProcessed.get());

        instantProcessingGateway.send(msg);

        assertTrue(reallyProcessed.get());
    }

}

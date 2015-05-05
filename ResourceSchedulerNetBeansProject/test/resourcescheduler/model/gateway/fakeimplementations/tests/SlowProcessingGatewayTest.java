package resourcescheduler.model.gateway.fakeimplementations.tests;

import resourcescheduler.model.gateway.fakeimplementations.SlowProcessingGateway;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class SlowProcessingGatewayTest {

    private static final long msToProcessMessage = 250;

    @Test
    public void testSend() throws InterruptedException {
        System.out.println("testSend");

        final Thread testThread = Thread.currentThread();
        boolean[] fastArray = new boolean[]{true, false};

        for (boolean fast : fastArray) {
            final int gatewayProcessingTime = (int) (fast ? msToProcessMessage / 3 : msToProcessMessage * 3);

            /*Don't mind atomic or not, just want a final object to place my values*/
            final AtomicReference<Float> milliBeforeSending = new AtomicReference<>(Float.valueOf(System.currentTimeMillis()));
            final AtomicReference<Float> millisAfterSending = new AtomicReference<>(Float.POSITIVE_INFINITY);

            final SlowProcessingGateway slowProcessingGateway = new SlowProcessingGateway(gatewayProcessingTime);

            slowProcessingGateway.send(new Message() {

                @Override
                public void completed() {
                    float millisNow = System.currentTimeMillis();
                    synchronized (testThread) {
                        testThread.notify();
                    }
                    /*If it was not final, I could not reference the object from inside Message*/
                    millisAfterSending.set(millisNow);
                }
            });

            synchronized (testThread) {

                testThread.wait((int) (msToProcessMessage * 1.5f));/*Not exact time, depends on JVM and OS states*/

                boolean itWasFast = ((millisAfterSending.get() - milliBeforeSending.get()) < msToProcessMessage);
                assertEquals(itWasFast, fast);//In one case it must be fast
            }
        }
    }
}

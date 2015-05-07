package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.message.DummyMessage;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class SlowProcessingGatewayTest {

    private static final long msToProcessMessage = 2000;

    @Test
    public void testSend() throws InterruptedException {
        System.out.println("testSend");

        final Thread testThread = Thread.currentThread();
        boolean[] fastArray = new boolean[]{true, false};

        for (boolean fast : fastArray) {
            final int gatewayProcessingTime
                    = (int) (fast ? msToProcessMessage / 3 : msToProcessMessage * 3);

            /*Don't mind atomic or not, just want a final object to place my values*/
            final AtomicReference<Float> milliBeforeSending
                    = new AtomicReference<>(Float.valueOf(System.currentTimeMillis()));

            final AtomicReference<Float> millisAfterSending
                    = new AtomicReference<>(Float.POSITIVE_INFINITY);

            final SlowProcessingGateway slowProcessingGateway
                    = new SlowProcessingGateway(gatewayProcessingTime);

            /*Explanation for that below... (#1)*/
            slowProcessingGateway.setInternalThreadFactory(new ThreadFactory() {
                final ThreadFactory tf = Executors.defaultThreadFactory();

                @Override
                public Thread newThread(Runnable r) {
                    Thread newThread = tf.newThread(r);
                    newThread.setPriority(Thread.MAX_PRIORITY);
                    return newThread;
                }
            });

            slowProcessingGateway.send(new DummyMessage() {

                @Override
                public void completed() {
                    float millisNow = System.currentTimeMillis();
                    synchronized (testThread) {
                        testThread.notify();
                    }
                    /*If it was not final, I could not reference the 
                     object from inside Message*/
                    millisAfterSending.set(millisNow);
                }
            });

            synchronized (testThread) {
                /*(#1) We want fast threads to pass the test.Not exact time, depends on JVM and OS states*/
                testThread.wait((int) (msToProcessMessage * 1.5f));
                final float millisTook = millisAfterSending.get() - milliBeforeSending.get();
                //System.out.println("millisTook = " + millisTook);
                boolean itWasFast = (millisTook < msToProcessMessage);
                assertEquals(itWasFast, fast);//In one case it must be fast
            }
        }
    }
}

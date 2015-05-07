package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.message.Message;

/**
 * Gateway whith fake resources that take different time delays
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class DifferentSpeedsProcessingGateway extends NotifyingGatewayImpl {

    private long processingMillis = 0;
    private ThreadFactory threadFactory;

    public DifferentSpeedsProcessingGateway() {
        threadFactory = Executors.defaultThreadFactory();
    }

    @Override
    public void send(Message msg) {
        super.send(msg);
        Thread newThread = threadFactory.newThread(new SlowMessageProcessorRunnable(msg));
        newThread.start();
    }

    public void setInternalThreadFactory(ThreadFactory threadFactory) {

        this.threadFactory = threadFactory;
    }

    public void setNextProcessingMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Needs positive number");
        }
        processingMillis = millis;
    }

    private class SlowMessageProcessorRunnable implements Runnable {

        private final Message msg;

        public SlowMessageProcessorRunnable(Message msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(processingMillis);

            } catch (InterruptedException ex) {
                Logger.getLogger(DifferentSpeedsProcessingGateway.class.getName()).log(Level.SEVERE, null, ex);
            }

            DifferentSpeedsProcessingGateway.super.setMessageCompleted(msg);
        }
    }
}

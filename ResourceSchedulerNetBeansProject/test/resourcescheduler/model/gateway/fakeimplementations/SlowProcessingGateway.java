package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class SlowProcessingGateway extends CompleteGateway {

    private final long processingMillis;
    private final ThreadFactory threadFactory;

    public SlowProcessingGateway(long processingMillis) {
        this.processingMillis = processingMillis;
        threadFactory = Executors.defaultThreadFactory();
    }

    @Override
    public void send(Message msg) {
        super.send(msg);
        Thread newThread = threadFactory.newThread(new SlowMessageProcessorRunnable(msg));

        newThread.start();
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
                Logger.getLogger(SlowProcessingGateway.class.getName()).log(Level.SEVERE, null, ex);
            }

            SlowProcessingGateway.super.setMessageCompleted(msg);
        }
    }
}

package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.ThreadFactory;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.message.Message;

/**
 * DifferentSpeedsProcessingGateway with fixed processing time (used Adapter
 * Pattern inside)
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class SlowProcessingGateway extends NotifyingGatewayImpl {

    private final DifferentSpeedsProcessingGateway differentSpeedsProcessingGateway;

    public SlowProcessingGateway(long processingMillis) {
        this.differentSpeedsProcessingGateway = new DifferentSpeedsProcessingGateway();
        this.differentSpeedsProcessingGateway.setNextProcessingMillis(processingMillis);
    }

    @Override
    public void send(Message msg) {
        differentSpeedsProcessingGateway.send(msg);
    }

    /**
     * Null means default
     *
     * @param threadFactory
     */
    public void setInternalThreadFactory(ThreadFactory threadFactory) {
        differentSpeedsProcessingGateway.setInternalThreadFactory(threadFactory);
    }

}

package resourcescheduler.model.gateway.fakeimplementations;

import java.util.concurrent.ThreadFactory;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.message.Message;

/**
 * Adapter Pattern
 *
 * @author Jaime Bárez Lobato
 */
public class SlowProcessingGateway extends CompleteGateway {
    
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

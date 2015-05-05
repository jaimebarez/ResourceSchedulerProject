package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class InstantProcessingGateway extends CompleteGateway {
    
    @Override
    public void send(Message msg) {
        super.send(msg);
        super.setMessageCompleted(msg);
    }
    
}

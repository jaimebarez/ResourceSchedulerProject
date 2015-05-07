package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class InstantProcessingGateway extends NotifyingGatewayImpl {
    
    @Override
    public void send(Message msg) {
        super.send(msg);
        super.setMessageCompleted(msg);
    }
    
}

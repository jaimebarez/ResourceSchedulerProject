package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.gateway.CompleteMsgNotifyingGateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class InstantProcessingGateway extends CompleteMsgNotifyingGateway {
    
    @Override
    public void send(Message msg) {
        
        msg.completed();
        super.fireMessageCompleted(msg);
    }
    
}

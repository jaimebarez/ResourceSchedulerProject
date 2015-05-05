package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class InstantProcessingGateway extends DummyGateway{

    @Override
    public final void send(Message msg) {
        super.send(msg);
        msg.completed();
    }
    
}

package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class DummyGateway extends NotifyingGatewayImpl {

    @Override
    public void send(Message msg) {
        super.send(msg);
        logMessageSent(msg);
    }

    private void logMessageSent(Message msg) {
        System.out.printf("Message '%s' sent\n", msg);
    }

}

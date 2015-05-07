package resourcescheduler.model.gateway.fakeimplementations;

import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.message.Message;

/**
 * This Gateway implementation has fake resources that instantly complete the
 * message processing
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class InstantProcessingGateway extends NotifyingGatewayImpl {

    @Override
    public void send(Message msg) {
        super.send(msg);
        super.setMessageCompleted(msg);
    }

}

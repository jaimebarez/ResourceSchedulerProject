package resourcescheduler.model.gateway.fakeimplementations;

import java.util.HashSet;
import java.util.Set;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.message.Message;

/**
 * This Gateway implementation has fake resources that process messages by
 * demand
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class ManualGateway extends NotifyingGatewayImpl {

    private final Set<Message> messages;

    public ManualGateway() {
        this.messages = new HashSet<>();
    }

    @Override
    public void send(Message msg) {
        super.send(msg);
        this.messages.add(msg);
    }

    /**
     * Processes message if it was sent
     *
     * @param msg
     * @return true if it was sent, so completed() was called
     */
    public boolean processSentMessage(Message msg) {
        final boolean removed = this.messages.remove(msg);
        if (removed) {
            super.setMessageCompleted(msg);
        }
        return removed;
    }

    public void processAllQueued() {
        //We need to make a copy to iterate while removing easily
        for (Message message : messages.toArray(new Message[messages.size()])) {
            processSentMessage(message);
        }
    }
}

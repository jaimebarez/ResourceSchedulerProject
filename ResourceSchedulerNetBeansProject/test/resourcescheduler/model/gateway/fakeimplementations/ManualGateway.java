package resourcescheduler.model.gateway.fakeimplementations;

import java.util.HashSet;
import java.util.Set;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ManualGateway extends DummyGateway {

    private final Set<Message> messages;

    public ManualGateway() {
        this.messages = new HashSet<>();
    }

    @Override
    public final void send(Message msg) {
        super.send(msg);
        addMessage(msg);
    }

    private void addMessage(Message msg) {
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
            msg.completed();
        }
        return removed;
    }

}

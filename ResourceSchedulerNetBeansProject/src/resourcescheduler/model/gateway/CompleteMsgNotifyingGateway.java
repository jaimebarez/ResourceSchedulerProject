package resourcescheduler.model.gateway;

import java.util.LinkedList;
import java.util.List;
import resourcescheduler.model.message.Message;

/**
 *
 * @author jaime.barez.lobato
 */
public abstract class CompleteMsgNotifyingGateway implements Gateway {

    private final List<MessageCompletedListener> listenersList;

    public CompleteMsgNotifyingGateway() {
        listenersList = new LinkedList<>();
    }

    @Override
    public abstract void send(Message msg);

    protected final void fireMessageCompleted(Message msg) {
        for (MessageCompletedListener mCL : listenersList) {
            mCL.notifyMessageCompleted(msg);
        }
    }

    public final boolean addOnMessageCompletedListener(MessageCompletedListener mCL) {
        return listenersList.add(mCL);
    }

    public final boolean removeOnMessageCompletedListener(MessageCompletedListener mCL) {
        return listenersList.remove(mCL);
    }

}

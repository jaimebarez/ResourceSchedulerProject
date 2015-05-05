package resourcescheduler.model.gateway;

import resourcescheduler.model.message.Message;

/**
 *
 * @author jaime.barez.lobato
 */
public interface MessageCompletedListener {

    public void notifyMessageCompleted(Message msg);
}

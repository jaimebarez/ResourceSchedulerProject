package resourcescheduler.model.gateway;

import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public interface MessageCompletedListener {

    public void notifyMessageCompleted(Message msg);
}

package resourcescheduler.model.gateway;

import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public interface Gateway {

    public void send(Message msg);
}

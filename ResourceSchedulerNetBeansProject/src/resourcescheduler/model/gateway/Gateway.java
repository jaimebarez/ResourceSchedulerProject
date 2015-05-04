package resourcescheduler.model.gateway;

import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public interface Gateway {

    public void send(Message msg);
}

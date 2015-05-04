package resourcescheduler.model;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class MessageFactory {

    public static Message createDummyMessage() {
        return new Message() {

            @Override
            public void completed() {

            }
        };
    }
}

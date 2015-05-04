package resourcescheduler.model.message;

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

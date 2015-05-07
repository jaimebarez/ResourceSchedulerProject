package resourcescheduler.resourcescheduler.exceptions;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class PreviousTermMsgReceivedException extends MessageReceivementException {

    private static final String errorMsg = "Previous message of that group was a termination message";

    public PreviousTermMsgReceivedException() {
        super(errorMsg);
    }
}

package ca.simplegames.micro.controllers;

/**
 * ControllerNotFoundException
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:01 PM)
 */
public class ControllerNotFoundException extends Exception {
    /**
     * Constructor for ControllerNotFoundException.
     */
    public ControllerNotFoundException() {
        super();
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param message
     */
    public ControllerNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param message
     * @param cause
     */
    public ControllerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param cause
     */
    public ControllerNotFoundException(Throwable cause) {
        super(cause);
    }
}

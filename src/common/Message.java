package common;


import java.io.Serializable;

/**
 * Base class for all messages.
 * Implements Serializable to allow for sending instances of
 * Message over TCP.
 */
public abstract class Message implements Serializable {

}

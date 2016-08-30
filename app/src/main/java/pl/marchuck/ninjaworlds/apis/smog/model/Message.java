package pl.marchuck.ninjaworlds.apis.smog.model;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class Message {

    public Integer type;
    public String message;

    @Override
    public String toString() {
        return "(" + type + "," + message + ")";
    }
}

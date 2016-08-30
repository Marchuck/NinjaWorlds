package pl.marchuck.ninjaworlds.apis.smog.model;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class SmogResponse {
    public Dane dane;


    @Override
    public String toString() {
        return dane.toString();
    }
}

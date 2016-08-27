package pl.marchuck.ninjaworlds.models;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public class Place {
    public String place;

    public Place(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return place;
    }
}

package pl.marchuck.ninjaworlds.apis.smog.model;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class Forecast {
    public TheDay wczoraj;
    public TheDay dzisiaj;
    public TheDay jutro;
    public TheDay pojutrze;

    @Override
    public String toString() {
        return "Forecast{" +
                "wczoraj=" + wczoraj +
                ", dzisiaj=" + dzisiaj +
                ", jutro=" + jutro +
                ", pojutrze=" + pojutrze +
                '}';
    }
}

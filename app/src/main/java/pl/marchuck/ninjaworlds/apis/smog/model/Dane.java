package pl.marchuck.ninjaworlds.apis.smog.model;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.util.LogUtil;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class Dane {
    public City city;
    public List<Actual> actual = new ArrayList<Actual>();
    public Forecast forecast;
    public Message message;

    @Override
    public String toString() {
        return "dane: " + city.toString() +
                ",\n actual=" + LogUtil.printList(actual) +
                ",\n forecast=" + forecast.toString() +
                ",\n message=" + message.toString();
    }
}

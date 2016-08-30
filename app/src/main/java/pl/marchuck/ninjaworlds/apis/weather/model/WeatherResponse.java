package pl.marchuck.ninjaworlds.apis.weather.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class WeatherResponse {
    public Latlng coords;
    public List<Weather> weather = new ArrayList<>();
    public String base;
    public int visibility;
    public Main main;
    public Wind wind;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public long id;
    public String name;
    public int cod;
}

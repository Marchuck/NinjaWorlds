package pl.marchuck.ninjaworlds.ui;

import android.support.annotation.IntDef;

/**
 * @author Lukasz Marczak
 * @since 28.08.16.
 */
public class WeatherInfo {

    public static final int NONE = -1;
    public static final int LITTLE = 0;
    public static final int NORMAL = 1;
    public static final int BIG = 2;
    public static final int CLEAN_SKY = 3;
    public static final int CLOUDY = 4;
    public static final int RAIN = 5;
    public static final int SNOW = 6;
    @Power
    private final int power;
    @Type
    private final int type;

    public WeatherInfo(@Power int power, @Type int type) {
        this.power = power;
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public int getType() {
        return type;
    }

    @IntDef({NONE, LITTLE, NORMAL, BIG,})
    public @interface Power {
    }

    @IntDef({CLEAN_SKY, RAIN, SNOW, CLOUDY,})
    public @interface Type {
    }
}

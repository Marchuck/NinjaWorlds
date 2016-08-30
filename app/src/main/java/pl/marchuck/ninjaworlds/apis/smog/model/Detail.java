package pl.marchuck.ninjaworlds.apis.smog.model;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class Detail {


    public String o_id;
    public String o_station;
    public String st_id;
    public String o_interval;
    public String o_wskaznik;
    public String par_id;
    public Integer o_value;
    public String o_czas;
    public String o_time;
    public String g_id;
    public String g_index;
    public String caqi_value;
    public String caqi_id;
    public String aqi_value;
    public String aqi_id;
    public String g_min_val;
    public String g_max_val;
    public String g_nazwa;
    public String par_name;
    public String par_desc;
    public String par_unit;
    public String par_html;
    public String parorder;
    public String max;

    @Override
    public String toString() {
        return "Detail{" +
                "o_id='" + o_id + '\'' +
                ", o_station='" + o_station + '\'' +
                ", st_id='" + st_id + '\'' +
                ", o_interval='" + o_interval + '\'' +
                ", o_wskaznik='" + o_wskaznik + '\'' +
                ", par_id='" + par_id + '\'' +
                ", o_value=" + o_value +
                ", o_czas='" + o_czas + '\'' +
                ", o_time='" + o_time + '\'' +
                ", g_id='" + g_id + '\'' +
                ", g_index='" + g_index + '\'' +
                ", caqi_value='" + caqi_value + '\'' +
                ", caqi_id='" + caqi_id + '\'' +
                ", aqi_value='" + aqi_value + '\'' +
                ", aqi_id='" + aqi_id + '\'' +
                ", g_min_val='" + g_min_val + '\'' +
                ", g_max_val='" + g_max_val + '\'' +
                ", g_nazwa='" + g_nazwa + '\'' +
                ", par_name='" + par_name + '\'' +
                ", par_desc='" + par_desc + '\'' +
                ", par_unit='" + par_unit + '\'' +
                ", par_html='" + par_html + '\'' +
                ", parorder='" + parorder + '\'' +
                ", max='" + max + '\'' +
                '}';
    }
}

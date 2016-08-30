package pl.marchuck.ninjaworlds.apis.smog.model;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.util.LogUtil;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class Actual {
    public String stationId;
    public String stationName;
    public Integer stationHour;
    public String stationMax;
    public List<Detail> details = new ArrayList<Detail>();

    @Override
    public String toString() {
        return "Actual{" +
                "stationId='" + stationId + '\'' +
                ", stationName='" + stationName + '\'' +
                ", stationHour=" + stationHour +
                ", stationMax='" + stationMax + '\'' +
                ", details=" + LogUtil.printList(details) +
                '}';
    }
}

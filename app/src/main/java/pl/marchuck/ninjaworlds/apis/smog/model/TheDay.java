package pl.marchuck.ninjaworlds.apis.smog.model;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.util.LogUtil;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class TheDay {

    public List<Average> averages = new ArrayList<Average>();
    public String max;
    public List<Detail> details = new ArrayList<Detail>();

    @Override
    public String toString() {
        return "TheDay{" +
                "averages=" + LogUtil.printList(averages) +
                ", max='" + max + '\'' +
                ", details=" + LogUtil.printList(details) +
                '}';
    }
}

package pl.marchuck.ninjaworlds.apis;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.jsoup.JsoupProxy;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class StopsAPI {

    public final String endpoint = "http://rozklady.mpk.krakow.pl";

    //todo: transform to rxcache
    public Observable<List<String>> provideStops() {
        return JsoupProxy.getJsoupDocument(endpoint + "/przystanek.php")
                .map(new Func1<Document, List<String>>() {
                    @Override
                    public List<String> call(Document document) {
                        Elements elements = document.getElementsByClass("label_submit");
                        JsoupProxy.printElements("STOPS", elements);
                        List<String> l = new ArrayList<>();
                        for (Element e : elements) {
                            l.add(e.text());
                        }
                        return l;
                    }
                });
    }
}

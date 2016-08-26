package pl.marchuck.ninjaworlds.jsoup;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */

public class JsoupProxy {
    public static rx.Observable<Document> getJsoupDocument(final String url) {
        return Observable.create(new Observable.OnSubscribe<Document>() {

            @Override
            public void call(Subscriber<? super Document> subscriber) {
                Document document = null;
                try {
                    document = Jsoup.connect(url).get();
                } catch (IOException ignored) {
                    Log.e("JsoupProxy", "getDocument: " + ignored.getMessage());
                }
                subscriber.onNext(document);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static void printElement(String TAG, Element e) {
        Log.d(TAG, "element: html: " + e.html() + ", text: " + e.text() + ", val: " + e.val() + ", data: "
                + e.data() + ", id: " + e.id() + ", nodeName: " + e.nodeName() + ", tag: " + e.tag()
                + ", tagName: " + e.tagName() + ", \n" + e.outerHtml());
    }

    public static void printElements(String TAG, Elements elements) {
        for (Element el : elements) printElement(TAG, el);
    }
}
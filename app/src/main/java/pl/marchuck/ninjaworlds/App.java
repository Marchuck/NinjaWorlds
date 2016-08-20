package pl.marchuck.ninjaworlds;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import rx.Subscriber;
import rx.schedulers.Schedulers;

import static pl.marchuck.ninjaworlds.RenderingImpl.loadTextures;

/**
 * @author Lukasz Marczak
 * @since 20.08.16.
 */
public class App extends Application {

    public static final String TAG = App.class.getSimpleName();
    public static boolean done;
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this.getApplicationContext();
        loadTextures(App.ctx)
                .observeOn(Schedulers.computation())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

}

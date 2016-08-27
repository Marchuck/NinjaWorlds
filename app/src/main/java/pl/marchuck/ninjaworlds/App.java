package pl.marchuck.ninjaworlds;

import android.app.Application;
import android.content.Context;

import pl.marchuck.ninjaworlds.stops_api.DataRepository;

/**
 * @author Lukasz Marczak
 * @since 20.08.16.
 */
public class App extends Application {

    public static final String TAG = App.class.getSimpleName();

    private DataRepository dataRepository;

    public static App getApp(Context context) {
        return ((App) context.getApplicationContext());
    }

    public static DataRepository getDataRepository(Context context) {
        return getApp(context).dataRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataRepository = new DataRepository(getCacheDir());
    }
}

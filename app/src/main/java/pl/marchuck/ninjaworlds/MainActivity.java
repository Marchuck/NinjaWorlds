package pl.marchuck.ninjaworlds;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.marchuck.ninjaworlds.apis.DataRepository;
import pl.marchuck.ninjaworlds.apis.smog.SmogAPI;
import pl.marchuck.ninjaworlds.apis.smog.model.SmogResponse;
import pl.marchuck.ninjaworlds.apis.weather.model.WeatherResponse;
import pl.marchuck.ninjaworlds.route_buttons.ButtonsFragment_;
import pl.marchuck.ninjaworlds.util.RotatingGLView;
import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        RotatingGLView.OnGLReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    public static final String TAG = MainActivity.class.getSimpleName();


    public GoogleApiClient apiClient;

    @ViewById(R.id.rootView)
    RelativeLayout rootView;

    RotatingGLView glView;

    @AfterViews
    void initViews() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        apiClient.registerConnectionCallbacks(this);
        apiClient.connect();
        Log.d(TAG, "initViews: ");


        SupportMapFragment su = new SupportMapFragment();
        DataRepository repo = App.getDataRepository(this);
        Observable.zip(getGoogleMap(su), repo.getWeatherConditions(), repo.getCurrentWeather(),
                new Func3<GoogleMap, SmogResponse, WeatherResponse, Boolean>() {
                    @Override
                    public Boolean call(final GoogleMap googleMap, SmogResponse smogResponse,
                                        WeatherResponse weatherResponse) {
                        double lat = weatherResponse.coords.lat;
                        double lon = weatherResponse.coords.lon;
                        final LatLng position = new LatLng(lat, lon);
                        //if (smogResponse.dane.)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(CameraPosition
                                                .fromLatLngZoom(position, 10)));
                            }
                        });
                        return true;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                        Log.d(TAG, "onNext: " + aBoolean);
                    }
                });

        su.getMapAsync(this);
        addFragment(su);

        SmogAPI api = new SmogAPI();
        api.getWeatherConditions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SmogResponse>() {
                    @Override
                    public void call(SmogResponse smogResponse) {
                        Log.d(TAG, "smogResponse: " + smogResponse.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "error: ", throwable);
                    }
                });

    }

    void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment: ");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootView, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        RotatingGLView.Builder builder = new RotatingGLView.Builder();
//        builder.rotationY = 0.03f;
//        builder.objPath = "cat/cat.obj";
//        builder.mtlPath = "cat/cat.mtl";
//        builder.texturePath = "cat/cat_diff.png";
//        glView = builder.build(this);
//        glView.getOnGLReady(this);
//        rootView.addView(glView);
        addFragment(new ButtonsFragment_());
        float den = getResources().getDisplayMetrics().density;
        Log.d(TAG, "density: " + den);
    }

    @Override
    public void onBackPressed() {
        getWindow().getDecorView().clearFocus();
        super.onBackPressed();
    }

    @Override
    public void onGLViewReady() {
        addFragment(new ButtonsFragment_());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + tname());
        Toast.makeText(MainActivity.this, "Cannot connect to google APIs ;__; ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + tname());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i + ", " + tname());
        Toast.makeText(MainActivity.this, "SUSPENDED ", Toast.LENGTH_SHORT).show();

    }

    String tname() {
        return Thread.currentThread().getName();
    }

    public Observable<GoogleMap> getGoogleMap(final SupportMapFragment mapFragment) {
        return Observable.fromAsync(new Action1<AsyncEmitter<GoogleMap>>() {
            @Override
            public void call(final AsyncEmitter<GoogleMap> mapEmitter) {
                if (mapFragment == null) mapEmitter.onError(new Throwable("Nullable mapFragment"));
                else
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mapEmitter.onNext(googleMap);
                            mapEmitter.onCompleted();
                        }
                    });
            }
        }, AsyncEmitter.BackpressureMode.LATEST);
    }
}

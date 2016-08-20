package pl.marchuck.ninjaworlds;

import android.opengl.GLSurfaceView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.fragment_main)
public class MainMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = MainMapFragment.class.getSimpleName();

    private GoogleMap map;

    @AfterInject
    public void inject() {

    }

    void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment: ");
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootView, fragment)
                .commitAllowingStateLoss();
    }

    @AfterViews
    public void views() {
        SupportMapFragment fragment = new SupportMapFragment();
        addFragment(fragment);
        addFragment(new GlEsFragment_());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        Toast.makeText(MainMapFragment.this.getActivity(), "onMapReady", Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(50, 19);
        map = googleMap;
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,
                16, 60, new Random().nextInt(360))));
    }
}

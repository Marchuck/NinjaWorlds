package pl.marchuck.ninjaworlds;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity  {
    public static final String TAG = MainActivity.class.getSimpleName();

    @AfterViews
    void aacscs() {

        Log.d(TAG, "aacscs: ");
        //addFragment(new DummyFragment_());
        addFragment(new GlEsFragment_());

    }

    void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment: ");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootView, fragment)
                .commitAllowingStateLoss();
    }

}

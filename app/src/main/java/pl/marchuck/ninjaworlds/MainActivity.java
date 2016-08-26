package pl.marchuck.ninjaworlds;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, RotatingGLView.OnGLReadyCallback {
    public static final String TAG = MainActivity.class.getSimpleName();

//    @ViewById(R.id.rotatingGLView)
//    RotatingGLView rotatingGLView;

    @ViewById(R.id.rootView)
    RelativeLayout rootView;

    RotatingGLView glView;

    @AfterViews
    void aacscs() {


        Log.d(TAG, "aacscs: ");
        SupportMapFragment su = new SupportMapFragment();
        su.getMapAsync(this);
        addFragment(su);
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
        RotatingGLView.Builder builder = new RotatingGLView.Builder();
        builder.rotationY = 0.03f;
        builder.objPath = "cat/cat.obj";
        builder.mtlPath = "cat/cat.mtl";
        builder.texturePath = "cat/cat_diff.png";
        glView = builder.build(this);

//        glView = new RotatingGLView(this);
        glView.getOnGLReady(this);
        rootView.addView(glView);
    }

    @Override
    public void onGLViewReady() {
        addFragment(new ButtonsFragment_());
    }
}

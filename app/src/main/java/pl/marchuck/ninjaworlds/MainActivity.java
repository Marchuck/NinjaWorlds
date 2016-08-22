package pl.marchuck.ninjaworlds;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.threed.jpct.TextureManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.rotating_glview_layout)
public class MainActivity extends AppCompatActivity implements RotatingGLView.ModelsLoader {
    public static final String TAG = MainActivity.class.getSimpleName();


    @ViewById(R.id.rotatingGLView)
    RotatingGLView rotatingGLView;
    @ViewById(R.id.rootView)
    RelativeLayout rootView;

    @AfterViews
    void aacscs() {
        Log.d(TAG, "aacscs: ");

        rotatingGLView.setModelsLoader(null);
        //  addFragment(new GlEsFragment_());


    }

    void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment: ");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootView, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void loadTextures(TextureManager textureManager) {
        // Context ctx = getApplicationContext();
        //  BitmapUtils.loadTextures(ctx);
    }
}

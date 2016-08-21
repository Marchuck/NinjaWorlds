package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.threed.jpct.TextureManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.kurde_layout)
public class MainActivity extends AppCompatActivity implements RotatingGLView.ModelsLoader {
    public static final String TAG = MainActivity.class.getSimpleName();


    @ViewById(R.id.rotatingGLView)
    RotatingGLView rotatingGLView;

    @AfterViews
    void aacscs() {
        Log.d(TAG, "aacscs: ");

        rotatingGLView.setModelsLoader(this);
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
        Context ctx = getApplicationContext();
        BitmapUtils.loadTextures(ctx);
    }
}

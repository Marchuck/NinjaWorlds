package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.activity_main)
public class DummyFragment extends Fragment {

    public static final String TAG = DummyFragment.class.getSimpleName();

    @ViewById(R.id.rootView)
    RelativeLayout relativeLayout;


    @AfterViews
    public void views() {
        Log.d(TAG, "views: ");
        relativeLayout.addView(new Button(getActivity()));
        relativeLayout.addView(new Button(getActivity()));
        relativeLayout.addView(new Button(getActivity()));
        relativeLayout.addView(new Button(getActivity()));
    }

}

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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.fragment_gles)
public class GlEsFragment extends Fragment implements OpenGLProxy, OpenGLHelper.ProgressIndicator , OnMapReadyCallback{

    public static final String TAG = GlEsFragment.class.getSimpleName();

    @ViewById(R.id.glesRelativeLayout)
    RelativeLayout relativeLayout;

    ProgressBar progressBar;
    OpenGLHelper openGLHelper;

    GLSurfaceView glSurfaceView;

    @AfterViews
    public void views() {
        Log.d(TAG, "views: ");
        SupportMapFragment mapfr = new SupportMapFragment();
        mapfr.getMapAsync(this);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.glesRelativeLayout,mapfr).commitAllowingStateLoss();
//        Toast.makeText(GlEsFragment.this.getActivity(), "onCreate GLES", Toast.LENGTH_SHORT).show();
//        GLSurfaceView glSurfaceView = new GLSurfaceView(getActivity());
//
//        glSurfaceView.setEGLContextClientVersion(2);
//        //prevent gles corruption
//        glSurfaceView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        renderer = new RenderingImpl(getActivity());
//        glSurfaceView.setRenderer(renderer);
//        //make transparent
//        //glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        relativeLayout.addView(glSurfaceView);

        glSurfaceView = new GLSurfaceView(getActivity());
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        openGLHelper = new OpenGLHelper(this);
        //   swipe.addListener(new SwipeCharacterListener(openGLHelper));

        Button btn = new Button(getActivity());
        btn.setText("switch");
        btn.setOnClickListener(openGLHelper);
        RelativeLayout.LayoutParams paramsForBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        paramsForBtn.addRule(CENTER_HORIZONTAL);

        progressBar = new ProgressBar(getActivity());
        openGLHelper.setProgressIndicator(this);
        RelativeLayout.LayoutParams paramsForProgress = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        paramsForProgress.addRule(CENTER_IN_PARENT);
        progressBar.setLayoutParams(paramsForProgress);
        progressBar.setVisibility(GONE);
        btn.setLayoutParams(paramsForBtn);

        relativeLayout.addView(glSurfaceView);
        relativeLayout.addView(progressBar);
        relativeLayout.addView(btn);
    }

    @Override
    public Context getBaseContext() {
        return getActivity();
    }

    @Override
    public GLSurfaceView getSurfaceView() {
        return glSurfaceView;
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Toast.makeText(getActivity(), "onMapReady", Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(50, 19);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,
                16, 60, new Random().nextInt(360))));
    }
}

package pl.marchuck.ninjaworlds;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.buttons_layout)
public class ButtonsFragment extends Fragment {

    public static final String TAG = ButtonsFragment.class.getSimpleName();

    @ViewById(R.id.from)
    ImageView fromButton;

    @ViewById(R.id.to)
    ImageView toButton;

    @ViewById(R.id.fromText)
    TextView fromText;

    @ViewById(R.id.toText)
    TextView toText;

    @ViewById(R.id.rootView)
    RelativeLayout rootView;

    @ViewById(R.id.swap)
    ImageView swapButton;
    private boolean clicksReady;
    private final Runnable buttonsActiveCallback = new Runnable() {
        @Override
        public void run() {
            clicksReady = true;
        }
    };

    @Click(R.id.from)
    void onFrom() {
        if (clicksReady) {

        }
    }

    @Click(R.id.to)
    void onTo() {
        if (clicksReady) {

        }
    }

    @Click(R.id.swap)
    void onSwap() {
        if (clicksReady) {

        }
    }

    @AfterViews
    public void views() {
        Log.d(TAG, "views: ");

        fromButton.setScaleY(0f);
        fromButton.setScaleX(0f);

        toButton.setScaleY(0f);
        toButton.setScaleX(0f);

        swapButton.setScaleY(0f);
        swapButton.setScaleX(0f);

        rootView.setVisibility(View.VISIBLE);


        fromButton.animate().scaleX(1).scaleY(1)
                .setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        toButton.animate().scaleX(1).scaleY(1)
                .setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(100)
                .start();

        swapButton.animate().scaleX(1).scaleY(1)
                .setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(200)
                .start();
        WeakHandler weakHandler = new WeakHandler();
        weakHandler.postDelayed(buttonsActiveCallback, 500);
    }


}

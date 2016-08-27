package pl.marchuck.ninjaworlds.route_buttons;

import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.util.WeakHandler;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.buttons_layout)
public class ButtonsFragment extends Fragment implements ButtonsCallbacks {

    public static final String TAG = ButtonsFragment.class.getSimpleName();
    public static final int FROM = 0;
    public static final int TO = 1;
    @ViewById(R.id.navigate)
    ImageView navigateButton;
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
    private ButtonsPresenter buttonsPresenter;
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

            buttonsPresenter.showDialog(fromText, FROM);
        }
    }

    @Click(R.id.to)
    void onTo() {
        if (clicksReady) {
            buttonsPresenter.showDialog(toText, TO);
        }
    }

    @Click(R.id.swap)
    void onSwap() {
        if (clicksReady) {
            Toast.makeText(ButtonsFragment.this.getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterViews
    public void initView() {
        Log.d(TAG, "initView: ");
        fromText.setTag("Select source");
        toText.setTag("Select destination");
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
        buttonsPresenter = new ButtonsPresenter(this);
    }

    @Override
    public void showNavigateButton() {
        navigateButton.setScaleY(0f);
        navigateButton.setScaleX(0f);
        navigateButton.setVisibility(View.VISIBLE);
        navigateButton.animate().scaleX(1).scaleY(1)
                .setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @IntDef({FROM, TO})
    public @interface Destination {

    }
}

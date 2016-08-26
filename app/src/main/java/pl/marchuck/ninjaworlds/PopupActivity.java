package pl.marchuck.ninjaworlds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.marchuck.ninjaworlds.util.MorphButtonToDialog;
import pl.marchuck.ninjaworlds.util.MorphDialogToButton;
import pl.marchuck.ninjaworlds.util.MorphDialogToFab;
import pl.marchuck.ninjaworlds.util.MorphFabToDialog;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */

@EActivity(R.layout.activity_popup)
public class PopupActivity extends AppCompatActivity {

    public static final String EXTRA_MORPH_TYPE = "morph_type";
    public static final String MORPH_TYPE_BUTTON = "morph_type_button";
    public static final String MORPH_TYPE_FAB = "morph_type_fab";
    boolean isDismissing = false;
    @ViewById(R.id.searchview)
    EditText editText;

    @ViewById(R.id.container)
    ViewGroup container;

    public static Intent getStartIntent(Context context, String type) {
        Intent intent = new Intent(context, PopupActivity_.class);
        intent.putExtra(EXTRA_MORPH_TYPE, type);
        return intent;
    }

    @AfterViews
    public void afterViews() {
        String type = getIntent().getStringExtra(EXTRA_MORPH_TYPE);
        if (type.equals(MORPH_TYPE_BUTTON)) {
            setupSharedElementTransitionsButton(this, container);
        } else if (type.equals(MORPH_TYPE_FAB)) {
            setupSharedElementTransitionsFab(this, container, 2);
        }
    }

    public void setupSharedElementTransitionsFab(@NonNull Activity activity,
                                                 @Nullable View target,
                                                 int dialogCornerRadius) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);
        int color = ContextCompat.getColor(activity, R.color.colorAccent);
        Interpolator easeInOut =
                AnimationUtils.loadInterpolator(activity, android.R.interpolator.fast_out_slow_in);
        MorphFabToDialog sharedEnter = new MorphFabToDialog(color, dialogCornerRadius);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);
        MorphDialogToFab sharedReturn = new MorphDialogToFab(color);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);
        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    public void setupSharedElementTransitionsButton(@NonNull Activity activity,
                                                    @Nullable View target) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);
        int color = ContextCompat.getColor(activity, R.color.colorAccent);
        Interpolator easeInOut =
                AnimationUtils.loadInterpolator(activity, android.R.interpolator.fast_out_slow_in);
        MorphButtonToDialog sharedEnter = new MorphButtonToDialog(color);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);
        MorphDialogToButton sharedReturn = new MorphDialogToButton(color);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);
        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void dismiss(View view) {
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

}

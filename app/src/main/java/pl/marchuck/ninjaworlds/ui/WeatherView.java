package pl.marchuck.ninjaworlds.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author Lukasz Marczak
 * @since 28.08.16.
 */
public class WeatherView extends View {
    public static final String TAG = WeatherView.class.getSimpleName();
    private static final int MAX_FLAKES = 250;
    private static final int DELAY = 10;
    private static int NUM_SNOWFLAKES = 50;
    private float densityFactor;
    private boolean isPaused;
    private Entity[] dropInUse;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public WeatherView(Context context) {
        super(context);
        densityFactor = context.getResources().getDisplayMetrics().density;
    }


    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        densityFactor = context.getResources().getDisplayMetrics().density;
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        densityFactor = context.getResources().getDisplayMetrics().density;
    }

    public void onPause() {
        isPaused = true;
    }

    public void onResume() {
        isPaused = false;
    }

    public void setCurrentWeather(WeatherInfo info) {
        @WeatherInfo.Power int power = info.getPower();
        NUM_SNOWFLAKES = DropCalculator.getDropSize(power);

    }

    protected void resize(int width, int height) {
        Log.d(TAG, "resize: " + width + ", " + height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        dropInUse = new Entity[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            dropInUse[i] = CloudEntity.create(width, height, densityFactor);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            resize(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPaused) return;
        for (Entity snowFlake : dropInUse) {
            snowFlake.draw(canvas);
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private static class DropCalculator {
        static int getDropSize(@WeatherInfo.Power int power) {
            return power == WeatherInfo.LITTLE ? MAX_FLAKES / 4
                    : power == WeatherInfo.NORMAL ? MAX_FLAKES / 2
                    : power == WeatherInfo.BIG ? MAX_FLAKES : 0;
        }
    }
}
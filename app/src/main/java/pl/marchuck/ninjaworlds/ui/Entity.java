package pl.marchuck.ninjaworlds.ui;

import android.graphics.Canvas;

/**
 * @author Lukasz Marczak
 * @since 28.08.16.
 */
public interface Entity {

    void draw(Canvas canvas);

    class Random {
        private static final java.util.Random RANDOM = new java.util.Random();

        public float getRandom(float lower, float upper) {
            float min = Math.min(lower, upper);
            float max = Math.max(lower, upper);
            return getRandom(max - min) + min;
        }

        public float getRandom(float upper) {
            return RANDOM.nextFloat() * upper;
        }

        public int getRandom(int upper) {
            return RANDOM.nextInt(upper);
        }

    }
}

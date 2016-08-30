package pl.marchuck.ninjaworlds.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @author Lukasz Marczak
 * @since 28.08.16.
 */
public class CloudEntity implements Entity {
    private static final float ANGE_RANGE = 0.1f;
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
    private static final float HALF_PI = (float) Math.PI / 2f;
    private static final float ANGLE_SEED = 10f;
    private static final float ANGLE_DIVISOR = 10000f;
    private static final float INCREMENT_LOWER = 2f;
    private static final float INCREMENT_UPPER = 4f;
    private static final float FLAKE_SIZE_LOWER = 7f;
    private static final float FLAKE_SIZE_UPPER = 20f;
    private final Random random;
    private final Point position;
    private final float increment;
    private final float flakeSize;
    private final Paint paint;
    private float densityFactor;
    private float angle;

    CloudEntity(Random random, Point position, float angle, float increment, float flakeSize, Paint paint, float densityFactor) {
        this.random = random;
        this.position = position;
        this.angle = angle;
        this.increment = increment;
        this.flakeSize = flakeSize;
        this.paint = paint;
        this.densityFactor = densityFactor;
    }

    public static CloudEntity create(int width, int height, float densityFactor) {
        Random random = new Random();
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setAlpha(190);

        int x = random.getRandom(width);
        int y = random.getRandom(height);
        Point position = new Point(x, y);
        float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);
        float flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER);
        return new CloudEntity(random, position, angle, increment, flakeSize, paint, densityFactor);
    }

    private void move(int width, int height) {
        double x = position.x + (increment * Math.cos(angle));
        double y = position.y + (increment * Math.sin(angle));

        angle += random.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR;

        position.set((int) x, (int) y);

        if (!isInside(width, height)) {
            reset(width);
        }
    }

    private boolean isInside(int width, int height) {
        int x = position.x;
        int y = position.y;
        return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height;
    }

    private void reset(int width) {
        position.x = random.getRandom(width);
        position.y = (int) (-flakeSize - 1);
        angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }

    @Override
    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        //todo: abstract this more

        int smallSubCloudRadius = 20;
        int cloudWidth = 70;
        int bigSubCloudRadius = 30;
        if (densityFactor < 3) {
            smallSubCloudRadius /= 3;
            cloudWidth /= 3;
            bigSubCloudRadius /= 3;
        }

        canvas.drawRect(position.x, position.y, position.x + cloudWidth, position.y + smallSubCloudRadius, paint);
        canvas.drawCircle(position.x - smallSubCloudRadius / 2, position.y, smallSubCloudRadius, paint);
        canvas.drawCircle(position.x + cloudWidth, position.y, smallSubCloudRadius, paint);
        canvas.drawCircle(position.x + cloudWidth / 2, position.y + smallSubCloudRadius - bigSubCloudRadius,
                bigSubCloudRadius, paint);
        //     canvas.drawRect(position.x, position.y, flakeSize, paint);
    }
}

package pl.marchuck.ninjaworlds.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Marczak
 * @since 28.08.16.
 */
public class RainEntity implements Entity {

    private static final float ANGE_RANGE = 0.1f;
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
    private static final float HALF_PI = (float) Math.PI / 2f;
    private static final float ANGLE_SEED = 25f;
    private static final float ANGLE_DIVISOR = 10000f;
    private static final float INCREMENT_LOWER = 2f;
    private static final float INCREMENT_UPPER = 4f;
    private static final float FLAKE_SIZE_LOWER = 7f;
    private static final float FLAKE_SIZE_UPPER = 20f;

    private final Random random;
    private final Point position;
    private final float increment;
    private final Paint paint;
    int LIMIT = 200;
    int randindex;
    int firstLoop;
    List<Path> paths = new ArrayList<>();
    List<Point> pathsOfCircles = new ArrayList<>();
    private float angle;
    private int flakeSize;

    RainEntity(Random random, Point position, float angle, float increment, float flakeSize, Paint paint) {
        this.random = random;
        this.position = position;
        this.angle = angle;
        this.increment = increment;
        // this.flakeSize = flakeSize;
        this.paint = paint;
    }

    public static RainEntity create(int width, int height) {
        Random random = new Random();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        paint.setAlpha(180);
        paint.setColor(Color.BLUE);
        int x = random.getRandom(width);
        int y = random.getRandom(height);
        Point position = new Point(x, y);
        float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);
        float flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER);
        return new RainEntity(random, position, angle, increment, flakeSize, paint);
    }

    private void move(int width, int height) {
        double x = position.x + (increment * Math.cos(angle));
        double y = position.y + (increment * Math.sin(angle));

        angle += random.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR;

        position.set((int) x, (int) y);
        randindex = (randindex + 1) % LIMIT;
        if (randindex < LIMIT) {
            firstLoop++;
        }
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
        position.y = -flakeSize - 1;
        angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }

    @Override
    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        flakeSize = 20;
        int S = 60;
        Path path;
        Point point;
        if (firstLoop < LIMIT) {
            Point p1 = new Point((position.x - flakeSize), position.y);
            Point p2 = new Point((position.x), (position.y - S));
            Point p3 = new Point((position.x + flakeSize), position.y);

            path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            path.lineTo(p3.x, p3.y);
            path.close();
            paths.add(path);
            point = new Point(position);
            pathsOfCircles.add(point);

        } else {
            if (randindex >= paths.size()) {
                path = paths.get(randindex - 1);
                point = pathsOfCircles.get(randindex - 1);

            } else {
                path = paths.get(randindex);
                point = pathsOfCircles.get(randindex);
            }
        }

        //canvas.drawCircle(position.x, position.y, flakeSize, paint);
        canvas.drawPath(path, paint);

        canvas.drawCircle(point.x, point.y, flakeSize, paint);
    }
}

package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.PowerManager;

import com.threed.jpct.Camera;
import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.marchuck.ninjaworlds.glfont.GLFont;
import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
public class NinjaWorldRenderer implements GLSurfaceView.Renderer {
    private SimpleVector ellipsoid = new SimpleVector(10, 10, 10);
    private SimpleVector translation = new SimpleVector(-5, 0, 0);

    private FrameBuffer frameBuffer = null;

    private GLFont glFont;
    private Context ctx;
    private float animationIndex = 0;

    public NinjaWorldRenderer() {
        Config.maxPolysVisible = 5000;
        Config.farPlane = 1500;
    }

    private World world = null;

    private AnimatedGroup ninja;
    private Object3D cube;

    private long frameTime = System.currentTimeMillis();
    private long aggregatedTime = 0;

    private String blitText;
    private long blitCountDown = 0;

    private PowerManager.WakeLock wakeLock;

    public NinjaWorldRenderer init(Context context) {
        this.ctx = context;
        if (world != null)
            return this;

        world = new World();

        cube = Primitives.getBox(10, 1);
        cube.translate(300, -100, 0);
        cube.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        world.addObject(cube);

        try {
            Resources res = context.getResources();
        //    ninja = BonesIO.loadGroup(res.openRawResource(R.raw.ninja));
            ninja.get(0).setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            ninja.addToWorld(world);

            ninja.get(0).addCollisionListener(new CollisionListener() {

                @Override
                public boolean requiresPolygonIDs() {
                    return false;
                }

                @Override
                public void collision(CollisionEvent ce) {
                    System.out.println("collided");
                    cube.translate(300, 0, 0);

                    blitText = "Collided";
                    blitCountDown = 500;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
          //  throw new RuntimeException(e);
        }

        world.buildAllObjects();

        world.setAmbientLight(127, 127, 127);
        world.buildAllObjects();

        new Light(world).setPosition(new SimpleVector(0, -400, 120));

        Camera camera = world.getCamera();
        camera.setPosition(0, -100, 500);
        camera.lookAt(new SimpleVector(0, -100, 0));
        camera.moveCamera(Camera.CAMERA_MOVERIGHT, -50);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Collision-Demo");
        return this;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        Logger.log("onSurfaceChanged");
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = new FrameBuffer(gl, w, h);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.log("onSurfaceCreated");

        TextureManager.getInstance().flush();
        Resources res = ctx.getResources();

       // Texture texture = new Texture(res.openRawResource(R.raw.ninja_texture));
      //  texture.keepPixelData(true);
        //TextureManager.getInstance().addTexture("ninja", texture);

        for (Animated3D a : ninja)
            a.setTexture("ninja");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create((String) null, Typeface.BOLD));

        paint.setTextSize(16);
        glFont = new GLFont(paint);

        paint.setTextSize(50);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (frameBuffer == null)
            return;


        long now = System.currentTimeMillis();
        aggregatedTime += (now - frameTime);
        frameTime = now;

        if (aggregatedTime > 1000) {
            aggregatedTime = 0;
        }


        while (aggregatedTime > GRANULARITY) {
            aggregatedTime -= GRANULARITY;

            animationIndex += 0.02f;
            while (animationIndex > 1)
                animationIndex -= 1;

            ninja.animateSkin(animationIndex, 20);

            cube.checkForCollisionEllipsoid(translation, ellipsoid, 1);
            cube.translate(translation);

            if (cube.getTranslation().x < -100) {
                cube.translate(400, 0, 0);

                blitText = "Missed";
                blitCountDown = 500;
            }

            blitCountDown -= GRANULARITY;
        }


        frameBuffer.clear();
        world.renderScene(frameBuffer);
        world.draw(frameBuffer);

        if (blitCountDown > 0) {
            glFont.blitString(frameBuffer, blitText, 10, 30, 10, RGBColor.WHITE);
        }

        frameBuffer.display();

    }

    private static final int GRANULARITY = 25;

}
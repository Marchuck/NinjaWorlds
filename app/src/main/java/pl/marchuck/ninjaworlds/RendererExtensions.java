package pl.marchuck.ninjaworlds;

/**
 * @author Lukasz Marczak
 * @since 19.08.16.
 */


import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 19.08.16.
 */
public abstract class RendererExtensions implements GLSurfaceView.Renderer {

    private float xpos, ypos, touchTurn, touchTurnUp;

    private boolean touchGesturesEnabled;

    private Object3D currentModel;

    private FrameBuffer frameBuffer;

    private World world;

    private Light sun;

    private int angZ, angY;
    private RGBColor backgroundColor = new RGBColor(50, 50, 100);
    private boolean isGles2_0;

    public void setBackGroundColor(RGBColor color) {
        this.backgroundColor = color;
    }

    public World getWorld() {
        return world;
    }

    /**
     * //todo: implement!
     */
    /**
     * dummy method for inform purposes
     */
    @Deprecated
    public abstract void rememberToLoadTexturesFirst();

    public abstract boolean setGesturesEnabled();

    public abstract rx.Observable<Object3D> loadModel();

    public abstract Action1<Throwable> onError();

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        world = new World();

        touchGesturesEnabled = setGesturesEnabled();
        isGles2_0 = is_GL_ES_2_0_supported();
        loadModel().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object3D>() {
                    @Override
                    public void call(Object3D object3D) {
                        currentModel = object3D;
                        final Camera camera = world.getCamera();
                        camera.moveCamera(Camera.CAMERA_MOVEOUT, 50);
                        Light sun = new Light(world);
                        sun.setIntensity(250, 250, 250);
                        camera.lookAt(currentModel.getTransformedCenter());
                        SimpleVector sv = new SimpleVector();
                        sv.set(currentModel.getTransformedCenter());
                        sv.y = -200;
                        sv.z = -200;
                        sun.setPosition(sv);
                    }
                }, onError());
    }

    protected abstract boolean is_GL_ES_2_0_supported();

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        if (isGles2_0) {
            frameBuffer = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
        } else {
            frameBuffer = new FrameBuffer(gl10, w, h); // OpenGL ES 1.x constructor
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (touchGesturesEnabled) {
            if (touchTurn != 0) {
                currentModel.rotateY(touchTurn);
                // cube[1].rotateY(touchTurn);
                touchTurn = 0;
            }

            if (touchTurnUp != 0) {
                currentModel.rotateX(touchTurnUp);
                // cube[1].rotateX(touchTurnUp);
                touchTurnUp = 0;
            }
        }
        if (frameBuffer != null) {
            frameBuffer.clear(backgroundColor);
            if (world != null) {
                world.renderScene(frameBuffer);
                world.draw(frameBuffer);
            }
            frameBuffer.display();
        }
//        sun.setPosition(SimpleVector.create(0,
//                (float) (200 * Math.cos(Math.toRadians(angY))),
//                (float) (200 * Math.sin(Math.toRadians(angZ)))));
//        angY += 1;
//        angZ += 1;
//        if (angY > 359) {
//            angY = angZ = 0;
//        }
    }

    private boolean onTouchEvent(MotionEvent me) {

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }

        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;

            xpos = me.getX();
            ypos = me.getY();

            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            return true;
        }

        try {
            Thread.sleep(30);
        } catch (Exception e) {
            // No need for this...
        }
        return false;
    }
}


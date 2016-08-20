package pl.marchuck.ninjaworlds;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


/**
 * @author Lukasz Marczak
 * @since 08.08.16.
 */
public class OpenGLHelper implements View.OnClickListener, Changeable {
    public static final String TAG = OpenGLHelper.class.getSimpleName();
    private boolean isGL20;
    private boolean isAdded;
    private boolean doOnceJobWithCamera;
    private Texture texFront, texBack;
    private int pressedTimes = 0;
    private Object3D currentModel;
    private Object3D rightModel, leftModel, centerModel;
    private World world;
    private AtomicBoolean nowIsSwitching = new AtomicBoolean(false);
    private ProgressIndicator progressIndicator;
    private MyRenderer renderer;


    private static boolean isGLES2_0(Context ctx) {
        final ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    @Override
    public void onChange(int swipingResult) {
        if (nowIsSwitching.get()) return;
        nowIsSwitching.set(true);
        Object3D obj = centerModel;
        if (swipingResult == 1) {
            obj = leftModel;
        } else if (swipingResult == 2) {
            obj = rightModel;
        }
        addObjectToWorld(world, obj);
        nowIsSwitching.set(false);
    }

    @Override
    public int getLastChange() {
        return renderer.swipingResult;
    }

    public interface ProgressIndicator {
        void showProgressBar();

        void hideProgressBar();
    }

    @Override
    public void onClick(View view) {
        pressedTimes = ((pressedTimes + 1) % 5);
        switch (pressedTimes) {
            case 0: {
                switchModel("joker", 1);
                break;
            }
            case 1: {
                switchModel("spider_man", 30);
                break;
            }
            case 2: {
                switchModel("puss_in_boots", 30);
                break;
            }
            case 3: {
                switchModel("shark", 10);
                break;
            }
            case 4: {
                switchModel("frizza", 10);
                break;
            }
        }
    }

    public void setProgressIndicator(ProgressIndicator indicator) {
        this.progressIndicator = indicator;
    }


    private void switchModel(String s, float f) {
        Log.d(TAG, "switchModel: ");
        if (world == null || nowIsSwitching.get()) return;
        nowIsSwitching.set(true);
        if (progressIndicator != null) progressIndicator.showProgressBar();
        //loadModel(s + ".obj", s + ".mtl", f / 2)
        RenderingImpl.loadCompletePokemon("haunter", 1f, openGLProxyRef.get().getBaseContext())
                .map(new Func1<Object3D, World>() {
                    @Override
                    public World call(Object3D object3D) {
                        return addObjectToWorld(world, object3D);
                    }
                }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<World>() {
                    @Override
                    public void onCompleted() {
                        nowIsSwitching.set(false);
                        if (progressIndicator != null) progressIndicator.hideProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(World world) {
                        Log.d(TAG, "onNext: ");
                    }
                });
    }

    public boolean onTouchEvent(MotionEvent me) {
        return renderer.onTouchEvent(me);
    }

    private static class MyRenderer implements GLSurfaceView.Renderer {
        final WeakReference<OpenGLHelper> helperWeakReference;

        private float xpos, ypos, touchTurn, touchTurnUp;

        private RGBColor back = new RGBColor(13, 88, 110, 0);
        private int fps;
        private long time;
        private FrameBuffer frameBuffer;
        private int swipingResult;

        public MyRenderer(OpenGLHelper helper) {
            helperWeakReference = new WeakReference<>(helper);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
            Log.e(TAG, "onSurfaceCreated: ");
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_FASTEST);

            gl.glClearColor(0, 0, 0, 0);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int w, int h) {
            Log.e(TAG, "onSurfaceChanged: ");
            final OpenGLHelper weakHelper = helperWeakReference.get();
            if (weakHelper == null || weakHelper.nowIsSwitching.get()) return;

            if (frameBuffer != null) {
                frameBuffer.dispose();
            }

            if (weakHelper.isGL20) {
                frameBuffer = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
            } else {
                frameBuffer = new FrameBuffer(gl10, w, h); // OpenGL ES 1.x constructor
            }
            if (weakHelper.world == null) {
                Observable.zip(weakHelper.initWorld(),

//                        weakHelper.loadModel("haunter.obj", "pikachu.mtl", 6f)
                        new RenderingImpl(App.ctx).loadModel(),
                        new Func2<World, Object3D, World>() {
                            @Override
                            public World call(World world, Object3D object3D) {
                                weakHelper.currentModel = object3D;
                                weakHelper.leftModel = object3D;
                                weakHelper.rightModel = object3D;
                                return weakHelper.addObjectToWorld(world, object3D);
                            }
                        }).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<World>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }

                            @Override
                            public void onNext(World world) {
                                Log.d(TAG, "onNext: world");
                            }
                        });
            }
        }

        private boolean isNotReady(@Nullable OpenGLHelper weakHelper) {
            return weakHelper == null || weakHelper.currentModel == null || weakHelper.nowIsSwitching.get();
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            Log.e(TAG, "onDrawFrame: " );
            OpenGLHelper weakHelper = helperWeakReference.get();
            if (isNotReady(weakHelper)) return;
            World world = weakHelper.world;
            if (touchTurn != 0) {
                weakHelper.currentModel.rotateY(touchTurn);
                // cube[1].rotateY(touchTurn);
                touchTurn = 0;
            }

            if (touchTurnUp != 0) {
                weakHelper.currentModel.rotateX(touchTurnUp);
                // cube[1].rotateX(touchTurnUp);
                touchTurnUp = 0;
            }

            if (frameBuffer != null) {
                frameBuffer.clear(back);
                if (world != null) {
                    world.renderScene(frameBuffer);
                    world.draw(frameBuffer);
                }
                frameBuffer.display();
            }
            if (!weakHelper.nowIsSwitching.get() && weakHelper.currentModel != null) {
                weakHelper.currentModel.rotateY(.03f);
                //  weakHelper.currentModel.
            }
            if (System.currentTimeMillis() - time >= 1000) {
                Logger.log(fps + "fps");
                fps = 0;
                time = System.currentTimeMillis();
            }
            fps++;
        }

        public boolean onTouchEvent(MotionEvent me) {

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

    public Observable<World> initWorld() {
        return Observable.fromAsync(new Action1<AsyncEmitter<World>>() {
            @Override
            public void call(AsyncEmitter<World> worldAsyncEmitter) {

                OpenGLProxy proxy = openGLProxyRef.get();

                if (proxy == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable OpenGLProxy reference"));
                    return;
                }
                if (proxy.getBaseContext() == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable OpenGLProxy context reference"));
                    return;
                }
                Resources res = proxy.getBaseContext().getResources();
                if (res == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable Resources  reference"));
                    return;
                }
                world = new World();
                world.setAmbientLight(20, 20, 20);

                Drawable drawableFront = res.getDrawable(R.drawable.__auto_);
                if (drawableFront == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable drawable"));
                    return;
                }
                texFront = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableFront), 256, 256));
                TextureManager.getInstance().addTexture("tex_front", texFront);

                Drawable drawableBack = res.getDrawable(R.drawable.__auto_1);
                if (drawableBack == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable drawable"));
                    return;
                }
                texBack = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableBack), 256, 256));
                TextureManager.getInstance().addTexture("tex_back", texBack);
                worldAsyncEmitter.onNext(world);
                worldAsyncEmitter.onCompleted();
            }
        }, AsyncEmitter.BackpressureMode.DROP);
    }

    public World addObjectToWorld(@NonNull final World world, @NonNull final Object3D object3D) {
        if (currentModel != null && isAdded) {
            world.removeAllObjects();
            isAdded = false;
            currentModel = null;
        }
        currentModel = object3D;
        isAdded = true;
        currentModel.rotateX((float) Math.PI);

        Camera cam = world.getCamera();

        //todo: fix this character rotation!
        if (!doOnceJobWithCamera) {
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
            Light sun = new Light(world);
            sun.setIntensity(250, 250, 250);
            cam.lookAt(currentModel.getTransformedCenter());
            SimpleVector sv = new SimpleVector();
            sv.set(currentModel.getTransformedCenter());
            sv.y = -200;
            sv.z = -200;
            sun.setPosition(sv);
            doOnceJobWithCamera = true;
        }

        MemoryHelper.compact();
        world.addObject(currentModel);
        return world;
    }

    public Observable<Object3D> loadModel(final String objFileName, final String mtlFileName, final float objScale) {
        return Observable.fromAsync(new Action1<AsyncEmitter<Object3D>>() {
            @Override
            public void call(AsyncEmitter<Object3D> object3DAsyncEmitter) {

                OpenGLProxy proxy = openGLProxyRef.get();

                if (proxy == null) {
                    object3DAsyncEmitter.onError(new Throwable("Nullable OpenGLProxy reference"));
                    return;
                }
                if (proxy.getBaseContext() == null) {
                    object3DAsyncEmitter.onError(new Throwable("Nullable OpenGLProxy context reference"));
                    return;
                }

                AssetManager assetManager = proxy.getBaseContext().getResources().getAssets();

                if (assetManager == null) {
                    object3DAsyncEmitter.onError(new Throwable("Nullable AssetManager reference"));
                    return;
                }

                Object3D out;
                Object3D[] outs;

                try {
                    outs = Loader.loadOBJ(assetManager.open(objFileName), assetManager.open(mtlFileName), objScale);
                    outs[0].setTexture("tex_back");
                    outs[1].setTexture("tex_front");
                    outs[0].build();
                    outs[1].build();
                    out = Object3D.mergeAll(outs);
                    out.build();
                    out.strip();
                } catch (IOException e) {
                    object3DAsyncEmitter.onError(e);
                    return;
                }

                object3DAsyncEmitter.onNext(out);
                object3DAsyncEmitter.onCompleted();
            }
        }, AsyncEmitter.BackpressureMode.LATEST);
    }

    private final WeakReference<OpenGLProxy> openGLProxyRef;

    public OpenGLHelper(@NonNull final OpenGLProxy openGLProxy) {
        this.openGLProxyRef = new WeakReference<>(openGLProxy);
        isGL20 = isGLES2_0(openGLProxy.getBaseContext());

        // GLSurfaceView gles = openGLProxy.getSurfaceView();
        openGLProxy.getSurfaceView().setEGLContextClientVersion(2);

        openGLProxy.getSurfaceView().setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        openGLProxy.getSurfaceView().getHolder().setFormat(PixelFormat.TRANSLUCENT);
        openGLProxy.getSurfaceView().setZOrderOnTop(true);
        renderer = new MyRenderer(this);
        openGLProxy.getSurfaceView().setRenderer(renderer);
    }
}

package pl.marchuck.ninjaworlds;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
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
import rx.subscriptions.CompositeSubscription;

/**
 * @author Lukasz Marczak
 * @since 08.08.16.
 */
public class RotatingGLView extends GLSurfaceView {
    public static final String TAG = RotatingGLView.class.getSimpleName();
    public float speedX, speedY = 0.03f, speedZ;
    public float rotationX, rotationY, rotationZ;
    public String objPath = "cat/cat.obj";
    @Nullable
    public String texturePath = "cat/cat_diff.png";
    public String mtlPath = "cat/cat.mtl";
    public float scale = 31f;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private AtomicBoolean nowIsSwitching = new AtomicBoolean(false);
    private boolean isGL20;
    private boolean isAdded;
    private boolean risingSun;
    private Object3D currentModel;
    private World world;
    private Light sun;
    private MyRenderer renderer;
    @Nullable
    private ProgressIndicator progressIndicator;
    private OnGLReadyCallback onGlReadyCallback;

    private RotatingGLView(Context c, Builder b) {
        super(c);
        texturePath = b.texturePath;
        mtlPath = b.mtlPath;
        objPath = b.objPath;
        speedX = b.speedX;
        speedY = b.speedY;
        speedZ = b.speedZ;
        rotationX = b.rotationX;
        rotationY = b.rotationY;
        rotationZ = b.rotationZ;
        scale = b.scale;

        init(c);
    }

    public RotatingGLView(Context context) {
        super(context);
        init(context);
    }

    public RotatingGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RotatingGLView, 0, 0);
        try {
            scale = a.getFloat(R.styleable.RotatingGLView_scale, 1f);
            speedY = a.getFloat(R.styleable.RotatingGLView_speedY, 0);
            speedZ = a.getFloat(R.styleable.RotatingGLView_speedZ, 0);
            rotationX = a.getFloat(R.styleable.RotatingGLView_rotationX, 0);
            rotationY = a.getFloat(R.styleable.RotatingGLView_rotationY, 0);
            rotationZ = a.getFloat(R.styleable.RotatingGLView_rotationZ, 0);
            objPath = a.getString(R.styleable.RotatingGLView_objPath);
            mtlPath = a.getString(R.styleable.RotatingGLView_mtlPath);
            texturePath = a.getString(R.styleable.RotatingGLView_texturePath);

            Log.d(TAG, "property: " + scale);//= a.getFloat(R.styleable.RotatingGLView_scale, 1f);
            Log.d(TAG, "property: " + speedY);//= a.getFloat(R.styleable.RotatingGLView_speedY, 0);
            Log.d(TAG, "property: " + speedZ);//= a.getFloat(R.styleable.RotatingGLView_speedZ, 0);
            Log.d(TAG, "property: " + rotationX);//= a.getFloat(R.styleable.RotatingGLView_rotationX, 0);
            Log.d(TAG, "property: " + rotationY);//= a.getFloat(R.styleable.RotatingGLView_rotationY, 0);
            Log.d(TAG, "property: " + rotationZ);//= a.getFloat(R.styleable.RotatingGLView_rotationZ, 0);
            Log.d(TAG, "property: " + objPath);//= a.getString(R.styleable.RotatingGLView_objPath);
            Log.d(TAG, "property: " + mtlPath);//= a.getString(R.styleable.RotatingGLView_mtlPath);
            Log.d(TAG, "property: " + texturePath);//= a.getString(R.styleable.RotatingGLView_texturePath);


        } finally {
            a.recycle();
        }
        init(context);
    }

    private static boolean isGLES2_0(Context ctx) {
        final ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    private rx.Observable<Object3D> loadModel() {
        return Observable.fromAsync(new Action1<AsyncEmitter<Object3D>>() {
            @Override
            public void call(AsyncEmitter<Object3D> object3DAsyncEmitter) {
                Log.e(TAG, "call [" + objPath + "," + mtlPath + "," + texturePath + "]");
                AssetManager assetManager = getContext().getAssets();
                Object3D out;
                Object3D[] outs;

                try {
                    outs = Loader.loadOBJ(assetManager.open(objPath),
                            assetManager.open(mtlPath), scale);
                    Log.i(TAG, "LOADED " + outs.length + " LAYERS " + texturePath);
                    if (Is.nonEmpty(texturePath))
                        outs[0].setTexture(texturePath);

                    outs[0].build();
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

    public RotatingGLView setProgressIndicator(@Nullable ProgressIndicator indicator) {
        this.progressIndicator = indicator;
        return this;
    }

    private void init(Context context) {
        setPreserveEGLContextOnPause(true);
        isGL20 = isGLES2_0(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //setZOrderOnTop(true);
        renderer = new MyRenderer(this);
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return renderer.onTouchEvent(me);
    }

    public Observable<World> initWorld(final Resources res) {
        return Observable.fromAsync(new Action1<AsyncEmitter<World>>() {
            @Override
            public void call(AsyncEmitter<World> worldAsyncEmitter) {
                if (res == null) {
                    worldAsyncEmitter.onError(new Throwable("Nullable Resources reference"));
                    return;
                }
                world = new World();
                world.setAmbientLight(20, 20, 20);
                worldAsyncEmitter.onNext(world);
                worldAsyncEmitter.onCompleted();
            }
        }, AsyncEmitter.BackpressureMode.LATEST);
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

        cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
        if (sun == null) sun = new Light(world);
        sun.setIntensity(250, 250, 250);
        SimpleVector modelVector = currentModel.getTransformedCenter();
        cam.lookAt(modelVector);
        SimpleVector sv = new SimpleVector(modelVector);
        sv.y = -200;
        sv.z = -200;
        sun.setPosition(sv);

        MemoryHelper.compact();
        world.addObject(currentModel);
        return world;
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        onDestroy();
        super.onDetachedFromWindow();
    }

    private void onDestroy() {
        //avoid leaks!
        MemoryHelper.compact();
        subscriptions.unsubscribe();
    }

    public void getOnGLReady(OnGLReadyCallback ref) {
        this.onGlReadyCallback = ref;
    }

    public interface OnGLReadyCallback {
        void onGLViewReady();
    }

    public interface ProgressIndicator {
        void showProgressBar();

        void hideProgressBar();
    }

    public static class Builder {

        public float speedX, speedY = 0.03f, speedZ;
        public float rotationX, rotationY, rotationZ;
        public String objPath = "cat/cat.obj";

        public String texturePath = "cat/cat_diff.png";
        public String mtlPath = "cat/cat.mtl";
        public float scale = 31f;

        public RotatingGLView build(Context c) {
            return new RotatingGLView(c, this);
        }
    }

    private static class MyRenderer implements GLSurfaceView.Renderer {
        final WeakReference<RotatingGLView> helperWeakReference;
        private float xpos, ypos, touchTurn, touchTurnUp;
        private RGBColor back = new RGBColor(13, 88, 110, 0);
        private int fps;
        private long time;
        private FrameBuffer frameBuffer;
        private int deg;
        private int timex;

        private MyRenderer(RotatingGLView helper) {
            helperWeakReference = new WeakReference<>(helper);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
            Log.d(TAG, "onSurfaceCreated: ");
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_FASTEST);
            gl.glClearColor(0, 0, 0, 0);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            final RotatingGLView weakHelper = helperWeakReference.get();
            if (weakHelper == null) {
                Log.e(TAG, "onSurfaceCreated: Nullable weakHelper");
                return;
            }
            if (weakHelper.progressIndicator != null)
                weakHelper.progressIndicator.showProgressBar();
            Context ctx = weakHelper.getContext();
            final Resources res = ctx.getResources();
            rx.Subscription subscription =
                    BitmapUtils.loadTexturesAsync(weakHelper.getContext(), new String[]{weakHelper.texturePath})
                            .onErrorResumeNext(new Func1<Throwable, Observable<? extends Boolean>>() {
                                @Override
                                public Observable<? extends Boolean> call(Throwable throwable) {
                                    return Observable.just(true);
                                }
                            }).flatMap(new Func1<Boolean, Observable<World>>() {
                        @Override
                        public Observable<World> call(Boolean aBoolean) {
                            return Observable.zip(weakHelper.initWorld(res),
                                    weakHelper.loadModel(),
                                    //load3dModel(weakHelper.getContext(), 20f, "cat", "cat/cat_diff.png"),
                                    new Func2<World, Object3D, World>() {
                                        @Override
                                        public World call(World world, Object3D object3D) {
                                            // weakHelper.leftModel = object3D;
                                            weakHelper.currentModel = object3D;
                                            // weakHelper.rightModel = object3D;
                                            return weakHelper.addObjectToWorld(world, object3D);
                                        }
                                    });
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
                                    if (weakHelper.currentModel == null) {
                                        onError(new Throwable("Nullable model"));
                                        return;
                                    }
                                    weakHelper.currentModel.translate(35, -25, 25);
                                    weakHelper.currentModel.rotateX(weakHelper.rotationX);
                                    weakHelper.currentModel.rotateY(weakHelper.rotationY);
                                    weakHelper.currentModel.rotateZ(weakHelper.rotationZ);
                                    if (weakHelper.onGlReadyCallback != null)
                                        weakHelper.onGlReadyCallback.onGLViewReady();
                                    if (weakHelper.progressIndicator != null)
                                        weakHelper.progressIndicator.hideProgressBar();
                                }
                            });
            weakHelper.subscriptions.add(subscription);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int w, int h) {
            Log.e(TAG, "onSurfaceChanged: ");
            final RotatingGLView weakHelper = helperWeakReference.get();
            if (weakHelper == null || weakHelper.nowIsSwitching.get()) return;

            if (frameBuffer != null) {
                frameBuffer.dispose();
            }

            if (weakHelper.isGL20) {
                frameBuffer = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
            } else {
                frameBuffer = new FrameBuffer(gl10, w, h); // OpenGL ES 1.x constructor
            }
        }

        private boolean isNotReady(@Nullable RotatingGLView weakHelper) {
            return weakHelper == null || weakHelper.currentModel == null || weakHelper.nowIsSwitching.get();
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // Log.e(TAG, "onDrawFrame: ");
            RotatingGLView weakRef = helperWeakReference.get();
            if (isNotReady(weakRef)) return;
            World world = weakRef.world;
            if (touchTurn != 0) {
                weakRef.currentModel.rotateY(touchTurn);
                touchTurn = 0;
            }

            if (touchTurnUp != 0) {
                weakRef.currentModel.rotateX(touchTurnUp);
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
            if (!weakRef.nowIsSwitching.get() && weakRef.currentModel != null) {
                weakRef.currentModel.rotateX(weakRef.speedX);
                weakRef.currentModel.rotateY(weakRef.speedY);
                weakRef.currentModel.rotateZ(weakRef.speedZ);
                if (weakRef.risingSun) {
                    if (timex == 2) {
                        timex = 0;
                        deg = (deg + 1) % (360);
                        weakRef.sun.setPosition(SimpleVector.create(0,
                                (float) (200 * Math.sin(Math.toRadians(deg))),
                                (float) (200 * Math.cos(Math.toRadians(deg)))));
                    }
                    timex++;
                }
            }
            if (System.currentTimeMillis() - time >= 5000) {
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
            } catch (Exception ignored) {
            }
            return false;
        }
    }
}

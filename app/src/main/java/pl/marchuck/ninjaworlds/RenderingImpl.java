package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;


import java.io.IOException;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 19.08.16.
 */
public class RenderingImpl extends RendererExtensions {

    public static final String TAG = RenderingImpl.class.getSimpleName();

    private Context ctx;

    public RenderingImpl() {

    }

    public RenderingImpl(Context ctx) {
        this.ctx = ctx;
        // setBackGroundColor(new RGBColor(20,20,20));
        setBackGroundColor(new RGBColor(0xff, 0xff, 0xff, 0xff));
    }

    public static rx.Observable<Boolean> loadTextures(final Context ctx) {
        return Observable.fromAsync(new Action1<AsyncEmitter<Boolean>>() {
            @Override
            public void call(AsyncEmitter<Boolean> booleanAsyncEmitter) {
                if (App.done) {
                    booleanAsyncEmitter.onNext(true);
                    booleanAsyncEmitter.onCompleted();
                    return;
                }

                TextureManager tm = TextureManager.getInstance();
                Resources res = ctx.getResources();

                Drawable drawableFront = res.getDrawable(R.drawable.ghost_dh);
                Drawable drawableBack = res.getDrawable(R.drawable.ghost_eye_dh);
                if (drawableFront == null || drawableBack == null) return;

                Texture texFront = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableFront), 256, 256));
                tm.addTexture("ghost_dh", texFront);

                Texture texBack = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableBack), 256, 128));
                tm.addTexture("ghost_eye_dh", texBack);
                App.done = true;
                booleanAsyncEmitter.onNext(true);
                booleanAsyncEmitter.onCompleted();
            }
        }, AsyncEmitter.BackpressureMode.DROP);
    }

    @Override
    public void rememberToLoadTexturesFirst() {

    }

    @Override
    public boolean setGesturesEnabled() {
        return true;
    }

    @Override
    public Observable<Object3D> loadModel() {
        Log.e(TAG, "loadModel: ");
        return loadCompletePokemon("haunter", 1f, ctx);
    }

    @Override
    public Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "call: ", throwable);
                throwable.printStackTrace();
            }
        };
    }

    @Override
    protected boolean is_GL_ES_2_0_supported() {
        return true;
    }

    public static rx.Observable<Object3D> loadCompletePokemon(final String name, float scale, Context ctx) {
        String prefix = name + "/";
        return loadPoke(prefix + name + ".obj",
                prefix + name + ".mtl", scale, ctx);
    }

    public static rx.Observable<Object3D> loadPoke(final String objFileName,
                                                   final String mtlFileName,
                                                   final float objScale,
                                                   final Context ctx) {
        return Observable.fromAsync(new Action1<AsyncEmitter<Object3D>>() {
            @Override
            public void call(AsyncEmitter<Object3D> object3DAsyncEmitter) {

                AssetManager assetManager = ctx.getResources().getAssets();

                if (assetManager == null) {
                    object3DAsyncEmitter.onError(new Throwable("Nullable AssetManager reference"));
                    return;
                }

                Object3D out;
                Object3D[] outs;

                try {
                    outs = Loader.loadOBJ(assetManager.open(objFileName), assetManager.open(mtlFileName), objScale);
                    Log.e(TAG, "WE WERE LOADED " + outs.length + " LAYERS");
                    outs[0].setTexture("ghost_eye_dh");

                    if (outs.length > 1) outs[1].setTexture("ghost_dh");

                    outs[0].build();
                    if (outs.length > 1) outs[1].build();

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

}

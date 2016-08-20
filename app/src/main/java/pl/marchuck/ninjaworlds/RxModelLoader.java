package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;

import java.io.IOException;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 16.08.16.
 */
public class RxModelLoader {
    public static final String TAG = RxModelLoader.class.getSimpleName();

    public static Observable<Object3D> loadModel(final String objFileName,
                                                 final String mtlFileName,
                                                 final String textureFileName,
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

    public static Observable<Object3D> loadModel(final String objFileName,
                                                 final String mtlFileName,
                                                 final float objScale, final Context ctx) {

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
}

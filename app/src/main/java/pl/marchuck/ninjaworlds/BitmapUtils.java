package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import java.io.IOException;
import java.io.InputStream;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 19.08.16.
 */
public class BitmapUtils {

    public static final String TAG = BitmapUtils.class.getSimpleName();

    public static rx.Subscription loadTextures(Context context) {
        return loadTexturesAsync(context)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    private static boolean loadNextTextureByPowersOf2(Context ctx, TextureManager tm, String path) {
        if (ctx == null || path == null) return false;
        Bitmap catBmp = getBitmapFromAsset(ctx, path);
        if (catBmp == null) return false;
        Texture catTexture = new Texture(catBmp);//bmp is already powered by 512x1024
        tm.addTexture("cat/cat_diff.png", catTexture);
        return true;
    }

    private static rx.Observable<Boolean> loadTexturesAsync(final Context ctx) {
        return Observable.fromAsync(new Action1<AsyncEmitter<Boolean>>() {
            @Override
            public void call(AsyncEmitter<Boolean> booleanAsyncEmitter) {

                TextureManager tm = TextureManager.getInstance();

                String[] tex = new String[]{"cat/cat_diff.png"};
                boolean result = true;
                int i = 0;
                for (; i < tex.length && result; i++) {
                    result = loadNextTextureByPowersOf2(ctx, tm, tex[i]);
                }
                if (!result) {
                    //something wrong happened, one or more textures are broken
                    String errorMessage = (i == 0) ?
                            "Cannot load any textures!" : "Nullable texture at " + tex[i - 1];
                    booleanAsyncEmitter.onError(new Throwable(errorMessage));
                    return;
                }
                //todo: scale bmps to fit powers of 2 if they are not
//                Drawable drawableFront = res.getDrawable(R.drawable.ghost_dh);
//                Drawable drawableBack = res.getDrawable(R.drawable.ghost_eye_dh);
//                Texture texFront = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableFront), 256, 256));
//                tm.addTexture("ghost_dh", texFront);
//                Texture texBack = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawableBack), 256, 128));
//                tm.addTexture("ghost_eye_dh", texBack);
                booleanAsyncEmitter.onNext(true);
                booleanAsyncEmitter.onCompleted();
            }
        }, AsyncEmitter.BackpressureMode.DROP);
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }
        return bitmap;
    }

}

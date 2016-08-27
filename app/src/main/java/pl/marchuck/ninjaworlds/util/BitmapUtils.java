package pl.marchuck.ninjaworlds.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.util.Log;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import java.io.IOException;
import java.io.InputStream;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 19.08.16.
 */
public class BitmapUtils {

    public static final String TAG = BitmapUtils.class.getSimpleName();

    public static rx.Subscription loadTextures(Context ctx, Subscriber<Boolean> sub) {
        return loadTexturesAsync(ctx, new String[]{"haunter/ghost_dh.png",
                        /* "haunter/ghost_eye_dh.png", "cat/cat_diff.png" */
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sub);
    }

    public static rx.Subscription loadTextures(Context context) {
        return loadTexturesAsync(context, new String[]{"haunter/ghost_dh.png",
                        /* "haunter/ghost_eye_dh.png", "cat/cat_diff.png" */
        })
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


    private static boolean loadNextTexture(Context ctx, TextureManager tm, String path) {
        if (ctx == null || path == null) return false;
        Bitmap catBmp = getBitmapFromAsset(ctx, path);
        if (catBmp == null) return false;
        Texture catTexture;
        if (isBitmapFaulty(catBmp)) {
            catTexture = new Texture(createFitBitmap(catBmp));
        } else {
            catTexture = new Texture(catBmp);//bmp is already powered by 512x1024
        }
        tm.addTexture(path, catTexture);
        return true;
    }

    public static rx.Observable<Boolean> loadTexturesAsync(final Context ctx, final String[] tex) {
        Log.d(TAG, "loadTexturesAsync: ");
        return Observable.fromAsync(new Action1<AsyncEmitter<Boolean>>() {
            @Override
            public void call(AsyncEmitter<Boolean> booleanAsyncEmitter) {

                TextureManager tm = TextureManager.getInstance();

                boolean result = true;
                int i = 0;
                for (; i < tex.length && result; i++) {
                    Log.d(TAG, "loading " + tex[i]);
                    if (Is.nonEmpty(tex[i])) result = loadNextTexture(ctx, tm, tex[i]);
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
        }, AsyncEmitter.BackpressureMode.LATEST);
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

    private static boolean isNotPowerOf2(int d) {
        return d != 1 && d != 2 && d != 4 && d != 8 && d != 16 && d != 32 && d != 64 && d != 128
                && d != 256 && d != 512 && d != 1024 && d != 2048 && d != 4096 && d != 8192;
    }

    public static boolean isBitmapFaulty(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        boolean faulty = false;
        if (isNotPowerOf2(w) && isNotPowerOf2(h)) {
            faulty = true;
        }
        return faulty;
    }

    private static Bitmap createTransparent(int width, int height) {

        int[] ints = new int[width * height];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = 0xff000000;
        }
        return Bitmap.createBitmap(ints, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap createFitBitmap(Bitmap bmp) {
        Log.d(TAG, "createFitBitmap: ");
        int fixedWidth = 1;
        int fixedHeight = 1;
        int inputWidth = bmp.getWidth();
        int inputHeight = bmp.getHeight();
        while (fixedHeight < inputHeight) fixedHeight *= 2;
        while (fixedWidth < inputWidth) fixedWidth *= 2;
        Log.d(TAG, "createFitBitmap: " + fixedWidth + " x " + fixedHeight);
        return BitmapTransform.createBitmap(createTransparent(fixedWidth, fixedHeight), bmp,
                PorterDuff.Mode.ADD, false, true);
    }
}

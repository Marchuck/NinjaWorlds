package pl.marchuck.ninjaworlds.experimantal;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class SearchViewEmitter extends SearchView implements TextEmitter {

    public SearchViewEmitter(Context context) {
        super(context);
    }

    public SearchViewEmitter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchViewEmitter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Observable<CharSequence> emit() {
        return Observable.fromAsync(new Action1<AsyncEmitter<CharSequence>>() {
            @Override
            public void call(final AsyncEmitter<CharSequence> charSequenceAsyncEmitter) {
                setOnQueryTextListener(new OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        charSequenceAsyncEmitter.onNext(newText);
                        return false;
                    }
                });
            }
        }, AsyncEmitter.BackpressureMode.LATEST);
    }
}

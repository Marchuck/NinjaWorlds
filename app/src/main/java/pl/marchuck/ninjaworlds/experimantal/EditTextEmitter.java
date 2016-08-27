package pl.marchuck.ninjaworlds.experimantal;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class EditTextEmitter extends EditText implements TextEmitter {

    public EditTextEmitter(Context context) {
        super(context);
    }

    public EditTextEmitter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextEmitter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextEmitter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public Observable<CharSequence> emit() {
        return Observable.fromAsync(new Action1<AsyncEmitter<CharSequence>>() {
            @Override
            public void call(final AsyncEmitter<CharSequence> charSequenceAsyncEmitter) {
                addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        charSequenceAsyncEmitter.onNext(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }, AsyncEmitter.BackpressureMode.LATEST);
    }
}

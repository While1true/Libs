package com.master.rxlib.Rx.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.SoftReference;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by 不听话的好孩子 on 2018/4/9.
 */

public class EditTextWatcher implements ObservableOnSubscribe<String>, TextWatcher {
    private SoftReference<EditText> set;
    ObservableEmitter<String> e;
    public EditTextWatcher(EditText et){
        set=new SoftReference<EditText>(et);
    }
    @Override
    public void subscribe(ObservableEmitter<String> e) throws Exception {
        this.e=e;
        if(set.get()!=null) {
            set.get().addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!e.isDisposed()){
            e.onNext(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

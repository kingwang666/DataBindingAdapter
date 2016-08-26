package com.example.jiudeng009.databindingadapter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import com.example.jiudeng009.databindingadapter.BR;

/**
 * Created by jiudeng009 on 2016/6/6.
 */

public class TextData extends BaseObservable{

    private String text;

    public TextData(String s) {
        text = s;
    }

    @Bindable
    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}

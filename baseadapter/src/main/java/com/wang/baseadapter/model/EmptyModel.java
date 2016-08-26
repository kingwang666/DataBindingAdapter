package com.wang.baseadapter.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.wang.baseadapter.BR;


/**
 * empty data model
 */

public class EmptyModel extends BaseObservable {

    private int imageRes;

    private String tip;

    public EmptyModel(int imageRes, String tip) {
        this.imageRes = imageRes;
        this.tip = tip;
    }

    public EmptyModel(int imageRes) {
        this.imageRes = imageRes;
        this.tip = "";
    }

    @Bindable
    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
        notifyPropertyChanged(BR.imageRes);
    }

    @Bindable
    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
        notifyPropertyChanged(BR.tip);
    }
}

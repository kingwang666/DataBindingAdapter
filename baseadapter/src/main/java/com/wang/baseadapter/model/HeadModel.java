package com.wang.baseadapter.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.wang.baseadapter.BR;


/**
 * head data model
 */

public class HeadModel extends BaseObservable {
    private int imageRes;

    private String tip;

    public HeadModel(int imageRes, String tip) {
        this.imageRes = imageRes;
        this.tip = tip;
    }

    public HeadModel(int imageRes) {
        this.imageRes = imageRes;
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

package com.example.jiudeng009.databindingadapter.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.jiudeng009.databindingadapter.BR;

/**
 * Created by wang
 * on 2016/11/11
 */

public class Section extends BaseObservable {

    public String name;

    public Section(String name) {
        this.name = name;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
}

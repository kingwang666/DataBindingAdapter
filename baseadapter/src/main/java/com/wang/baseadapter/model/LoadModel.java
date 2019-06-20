package com.wang.baseadapter.model;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.wang.baseadapter.BR;


/**
 * load data model
 */

public class LoadModel extends BaseObservable{

    private boolean noData;
    private String tip;

    public LoadModel(){
        noData = true;
        tip = "没有更多数据";
    }

    public LoadModel(boolean isNoData){
        noData = isNoData;
    }

    public LoadModel(boolean isNoData, String tip){
        noData = isNoData;
        this.tip = tip;
    }

    @Bindable
    public boolean isNoData() {
        return noData;
    }

    public void setNoData(boolean noData) {
        this.noData = noData;
        notifyPropertyChanged(BR.noData);
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

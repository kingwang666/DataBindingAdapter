package com.wang.baseadapter.model;


import java.io.Serializable;

/**
 * 列表数据类
 */
public class ItemData<T> implements Serializable {
    private int mDataType;
    private T mData;

    public ItemData(int dataType, T data) {
        this.mDataType = dataType;
        this.mData = data;
    }

    public ItemData() {
    }

    public int getDataType() {
        return mDataType;
    }

    public void setDataType(int dataType) {
        this.mDataType = dataType;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        this.mData = data;
    }
}

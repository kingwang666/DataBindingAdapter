package com.wang.baseadapter.model;


import java.io.Serializable;

/**
 * 列表数据类
 */
public class ItemData implements Serializable {
    private int mDataType;
    private Object mData;

    public ItemData(int dataType, Object data) {
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

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }
}

package com.example.jiudeng009.databindingadapter.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.jiudeng009.databindingadapter.BR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang
 * on 2016/11/11
 */

public class Chapter extends BaseObservable {

    private String name;

    private int sectionSize;

    private List<Section> sections;

    private boolean isOpen;

    public Chapter(String name, int sectionSize) {
        this.name = name;
        this.sectionSize = sectionSize;
        this.sections = new ArrayList<>(sectionSize);
        this.isOpen = true;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public int getSectionSize() {
        return sectionSize;
    }

    public void setSectionSize(int sectionSize) {
        this.sectionSize = sectionSize;
        notifyPropertyChanged(BR.sectionSize);
    }

    @Bindable
    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
        notifyPropertyChanged(BR.sections);
    }

    @Bindable
    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
        notifyPropertyChanged(BR.open);
    }
}

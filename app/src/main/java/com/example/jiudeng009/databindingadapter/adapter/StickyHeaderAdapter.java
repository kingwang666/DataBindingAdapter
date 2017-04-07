package com.example.jiudeng009.databindingadapter.adapter;

import android.view.View;

import com.example.jiudeng009.databindingadapter.R;
import com.example.jiudeng009.databindingadapter.databinding.ItemChapterBinding;
import com.example.jiudeng009.databindingadapter.databinding.ItemSectionBinding;
import com.example.jiudeng009.databindingadapter.interfaces.OnRecyclerViewClickListener;
import com.example.jiudeng009.databindingadapter.model.Chapter;
import com.example.jiudeng009.databindingadapter.model.Section;
import com.wang.baseadapter.BaseRecyclerViewAdapter;
import com.wang.baseadapter.BaseViewHolder;
import com.wang.baseadapter.model.RecyclerViewItemArray;

/**
 * Created on 2016/11/13.
 * Author: wang
 */

public class StickyHeaderAdapter extends BaseRecyclerViewAdapter {

    public static final int TYPE_CHAPTER = 1;
    public static final int TYPE_SECTION = 2;

    private OnRecyclerViewClickListener mListener;

    public StickyHeaderAdapter(RecyclerViewItemArray itemArray, OnRecyclerViewClickListener listener) {
        super(itemArray);
        addItemType(TYPE_CHAPTER, R.layout.item_chapter);
        addItemType(TYPE_SECTION, R.layout.item_section);
        addNoAnimType(TYPE_CHAPTER);
        mListener = listener;
    }

    @Override
    protected void onBindDefViewHolder(BaseViewHolder holder, RecyclerViewItemArray itemArray, int position, int viewType) {
        switch (viewType) {
            case TYPE_CHAPTER: {
                ItemChapterBinding binding = (ItemChapterBinding) holder.binding;
                binding.setChapter((Chapter) itemArray.get(position).getData());
                break;
            }
            case TYPE_SECTION: {
                ItemSectionBinding binding = (ItemSectionBinding) holder.binding;
                binding.setSection((Section) itemArray.get(position).getData());
                break;
            }
        }
    }

    @Override
    protected void afterView(BaseViewHolder vh, int viewType) {

    }

    @Override
    protected void initItemListener(final BaseViewHolder vh, final int viewType) {
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(viewType, vh.getAdapterPosition(), null);
            }
        });
    }
}

package com.example.jiudeng009.databindingadapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.example.jiudeng009.databindingadapter.databinding.ItemTextBinding;
import com.wang.baseadapter.BaseRecyclerViewAdapter;
import com.wang.baseadapter.BaseViewHolder;
import com.wang.baseadapter.model.ItemData;
import com.wang.baseadapter.model.RecyclerViewItemArray;

/**
 * Created on 2016/6/6.
 * Author: wang
 */

public class TextAdapter extends BaseRecyclerViewAdapter {



    public TextAdapter(RecyclerViewItemArray itemArray) {
        super(itemArray);
        addItemType(0, R.layout.item_text);
    }

    @Override
    protected void onBindDefViewHolder(BaseViewHolder holder, RecyclerViewItemArray itemArray, int position, int viewType) {
        if (viewType == 0){
            ItemTextBinding binding = (ItemTextBinding) holder.binding;
            binding.setData((TextData) itemArray.get(position).getData());
        }
    }

    @Override
    protected void initItemListener(final BaseViewHolder vh, int viewType) {
        super.initItemListener(vh, viewType);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", vh.getAdapterPosition() + "");
            }
        });
    }

}

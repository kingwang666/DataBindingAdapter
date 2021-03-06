package com.wang.baseadapter;

import com.wang.baseadapter.model.ItemArray;
import com.wang.baseadapter.util.SwipeAdapterInterface;
import com.wang.baseadapter.util.SwipeItemMangerImpl;
import com.wang.baseadapter.util.SwipeItemMangerInterface;
import com.wang.baseadapter.widget.SwipeItemView;

import java.util.List;


public abstract class RecyclerSwipeAdapter extends BaseRecyclerViewAdapter implements SwipeItemMangerInterface, SwipeAdapterInterface {

    protected SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

    public RecyclerSwipeAdapter(ItemArray itemArray) {
        super(itemArray);
    }


    @Override
    protected void onBindDefViewHolder(BaseViewHolder holder, ItemArray itemArray, int position, int viewType) {
        if (holder.itemView instanceof SwipeItemView){
            mItemManger.bind((SwipeItemView) holder.itemView, position);
        }
    }

    @Override
    public void notifyDatasetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeItemView layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeItemView> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeItemView layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public SwipeItemView.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(SwipeItemView.Mode mode) {
        mItemManger.setMode(mode);
    }
}

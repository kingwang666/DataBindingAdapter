
package com.wang.baseadapter;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import com.wang.baseadapter.animation.AlphaInAnimation;
import com.wang.baseadapter.animation.BaseAnimation;
import com.wang.baseadapter.animation.ScaleInAnimation;
import com.wang.baseadapter.animation.SlideInBottomAnimation;
import com.wang.baseadapter.animation.SlideInLeftAnimation;
import com.wang.baseadapter.animation.SlideInRightAnimation;
import com.wang.baseadapter.model.ItemData;
import com.wang.baseadapter.model.RecyclerViewItemArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<BaseViewHolder<T>> {


    /**
     * header type
     */
    public static final int TYPE_HEADER = Integer.MAX_VALUE - 1;
    /**
     * loading type
     */
    public static final int TYPE_LOADING = Integer.MAX_VALUE - 2;
    /**
     * footer type
     */
    public static final int TYPE_FOOTER = Integer.MAX_VALUE - 3;
    /**
     * empty type
     */
    public static final int TYPE_EMPTY = Integer.MAX_VALUE - 4;

    @IntDef({ALPHA_IN, SCALE_IN, SLIDE_IN_BOTTOM, SLIDE_IN_LEFT, SLIDE_IN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {

    }

    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHA_IN = 1;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALE_IN = 2;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_BOTTOM = 3;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_LEFT = 4;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_RIGHT = 5;

    private boolean mNextLoadEnable = false;
    private boolean mLoadingMoreEnable = false;

    private boolean mFirstOnlyEnable = true;
    private boolean mOpenAnimationEnable = false;

    private Interpolator mInterpolator = new LinearInterpolator();
    private int mDuration = 300;
    private int mLastPosition = -1;
    private RequestLoadMoreListener mRequestLoadMoreListener;

    private BaseAnimation mSelectAnimation = new AlphaInAnimation();

    private List<Integer> mNoAnimTypes;

    /**
     * layouts indexed with their types
     */
    private SparseIntArray layouts;

    protected RecyclerViewItemArray mItemArray;



    public BaseRecyclerViewAdapter(RecyclerViewItemArray itemArray) {
        this.mItemArray = itemArray == null ? new RecyclerViewItemArray() : itemArray;
        layouts = new SparseIntArray();
        mNoAnimTypes = new ArrayList<>();
    }

    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
    }

    public void addNoAnimType(int type){
        mNoAnimTypes.add(type);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }


    /**
     * call the method before you should call setPageSize() method to setting up the enablePagerSize value,whether it will  invalid
     * enable the loading more data function if enable's value is true,or disable
     *
     * @param enable
     */
    public void openLoadMore(boolean enable) {
        mNextLoadEnable = enable;

    }

    /**
     * 添加对应的type和其layout
     *
     * @param type        类型
     * @param layoutResId layout
     */
    protected void addItemType(int type, int layoutResId) {
        layouts.put(type, layoutResId);
    }

    /**
     * 获取对应的layout
     *
     * @param type 类型
     * @return layoutId
     */
    protected Integer getItemLayout(int type) {
        return layouts.get(type);
    }

    /**
     * 底部自动加载更多后notify
     *
     * @param type 插入数据类型
     * @param data 插入的数据
     * @param <E>  数据类
     */
    public <E> void notifyDataChangedAfterLoadMore(int type, List<E> data) {
        notifyDataChangedAfterLoadMore(type, data, mNextLoadEnable);
    }

    /**
     * 底部自动加载更多后notify
     *
     * @param type       插入数据类型
     * @param data       插入的数据
     * @param <E>        数据类
     * @param isNextLoad 下次滑到底部是否还自动加载
     */
    public <E> void notifyDataChangedAfterLoadMore(int type, List<E> data, boolean isNextLoad) {
        for (E e : data) {
            mItemArray.add(mItemArray.size() - 1, new ItemData<>(type, e));
        }
        notifyDataChangedAfterLoadMore(isNextLoad);

    }

    /**
     * 底部自动加载更多后notify
     *
     * @param isNextLoad 下次滑到底部是否还自动加载
     */
    public void notifyDataChangedAfterLoadMore(boolean isNextLoad) {
        mNextLoadEnable = isNextLoad;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();
    }

    /**
     * 获取列表数据
     *
     * @return list
     */
    public RecyclerViewItemArray getItemArray() {
        return mItemArray;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public ItemData getItem(int position) {
        return mItemArray.get(position);
    }


    @Override
    public int getItemCount() {
        return mItemArray.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mItemArray.get(position).getDataType();
    }

    private static final Object DB_PAYLOAD = new Object();
    @Nullable
    private RecyclerView mRecyclerView;

    /**
     * This is used to block items from updating themselves. RecyclerView wants to know when an
     * item is invalidated and it prefers to refresh it via onRebind. It also helps with performance
     * since data binding will not update views that are not changed.
     */
    private final OnRebindCallback mOnRebindCallback = new OnRebindCallback() {
        @Override
        public boolean onPreBind(ViewDataBinding binding) {
            if (mRecyclerView == null || mRecyclerView.isComputingLayout()) {
                return true;
            }
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(binding.getRoot());
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }
            notifyItemChanged(childAdapterPosition, DB_PAYLOAD);
            return false;
        }
    };

    @Override
    @CallSuper
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder<T> baseViewHolder;
        switch (viewType) {
            case TYPE_LOADING:
                baseViewHolder = getLoadingView(parent);
                break;
            case TYPE_HEADER:
                baseViewHolder = getHeadView(parent);
                break;
            case TYPE_EMPTY:
                baseViewHolder = getEmptyView(parent);
                break;
            case TYPE_FOOTER:
                baseViewHolder = getFooterView(parent);
                break;
            default:
                baseViewHolder = onCreateDefViewHolder(parent, viewType);
                break;
        }
        afterView(baseViewHolder, viewType);
        initItemListener(baseViewHolder, viewType);
        baseViewHolder.binding.addOnRebindCallback(mOnRebindCallback);
        return baseViewHolder;
    }

    @Override
    public final void onBindViewHolder(BaseViewHolder<T> holder, int position, List<Object> payloads) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            bindItem(holder, position, payloads);
        }
        holder.binding.executePendingBindings();
    }


    private void bindItem(BaseViewHolder<T> holder, int position, List<Object> payloads) {
        int type = holder.getItemViewType();
        switch (type) {
            case TYPE_LOADING:
                onBindLoadViewHolder(holder, mItemArray.get(position));
                addLoadMore(holder);
                break;
            case TYPE_HEADER:
                onBindHeadViewHolder(holder, mItemArray.get(position));
                break;
            case TYPE_EMPTY:
                onBindEmptyViewHolder(holder, mItemArray.get(position));
                break;
            case TYPE_FOOTER:
                onBindFooterViewHolder(holder, mItemArray.get(position));
                break;
            default:
                onBindDefViewHolder(holder, mItemArray, position, getItemViewType(position));
                addAnimation(holder, type);
                break;
        }
    }

    /**
     * 绑定数据到footer
     *
     * @param holder viewHolder
     * @param data   数据
     */
    protected void onBindFooterViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(com.wang.baseadapter.BR.footer, data.getData());
    }

    /**
     * 绑定数据到empty
     *
     * @param holder viewHolder
     * @param data   数据
     */
    protected void onBindEmptyViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.empty, data.getData());
    }

    /**
     * 绑定数据到head
     *
     * @param holder viewHolder
     * @param data   数据
     */
    protected void onBindHeadViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.head, data.getData());
    }

    /**
     * 绑定数据到load
     *
     * @param holder viewHolder
     * @param data   数据
     */
    protected void onBindLoadViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.load, data.getData());
    }

    /**
     * 绑定数据到自定义视图
     *
     * @param holder    viewHolder
     * @param itemArray 数据
     * @param position
     */
    protected abstract void onBindDefViewHolder(BaseViewHolder<T> holder, RecyclerViewItemArray itemArray, int position, int viewType);


    /**
     * 查询数据是否需要binding
     *
     * @param payloads 数据
     * @return true需要， false不需要
     */
    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        throw new IllegalArgumentException("just overridden to make final.");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager){
            final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
            if (manager.getSpanSizeLookup() instanceof GridLayoutManager.DefaultSpanSizeLookup){
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = mItemArray.get(position).getDataType();
                        if (type >= TYPE_EMPTY){
                            return manager.getSpanCount(); //宽度为整个recycler的宽度
                        }
                        return 1;
                    }
                });
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
    }


    private BaseViewHolder<T> getEmptyView(ViewGroup parent) {
        Integer layoutId = getItemLayout(TYPE_EMPTY);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_empty_view : layoutId);
    }

    private BaseViewHolder<T> getHeadView(ViewGroup parent) {
        Integer layoutId = getItemLayout(TYPE_HEADER);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_head_view : layoutId);
    }

    private BaseViewHolder<T> getFooterView(ViewGroup parent) {
        Integer layoutId = getItemLayout(TYPE_FOOTER);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_footer_view : layoutId);
    }

    private BaseViewHolder<T> getLoadingView(ViewGroup parent) {
        Integer layoutId = getItemLayout(TYPE_LOADING);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_loading : layoutId);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type >= TYPE_EMPTY) {
            setFullSpan(holder);
        }
    }

    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }


    protected BaseViewHolder<T> onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.create(parent, getItemLayout(viewType));
    }

    /**
     * 调用加载更多请求接口
     *
     * @param holder
     */
    private void addLoadMore(RecyclerView.ViewHolder holder) {
        if (isLoadMore()) {
            mLoadingMoreEnable = true;
            mRequestLoadMoreListener.onLoadMoreRequested();
        }
    }

    protected void afterView(final BaseViewHolder vh, int viewType) {

    }

    /**
     * 初始化item接听接口
     *
     * @param vh       对应的viewHolder
     * @param viewType 对应的type
     */
    protected void initItemListener(final BaseViewHolder vh, int viewType) {

    }

    public void resetAnimPostion(){
        mLastPosition = -1;
    }

    /**
     * 加入并开始动画
     *
     * @param holder 对应的viewHolder
     */
    private void addAnimation(RecyclerView.ViewHolder holder, int type) {
        if (mOpenAnimationEnable && mSelectAnimation != null && !mNoAnimTypes.contains(type)) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                AnimatorSet set = mSelectAnimation.getAnimators(holder.itemView);
                set.setDuration(mDuration);
                set.setInterpolator(mInterpolator);
                set.start();
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }


    /**
     * 判断是否需要加载更多
     *
     * @return true需要
     */
    private boolean isLoadMore() {
        return mNextLoadEnable && !mLoadingMoreEnable && mRequestLoadMoreListener != null;
    }


    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();
    }


    /**
     * Set the view animation type.
     *
     * @param animationType One of {@link #ALPHA_IN}, {@link #SCALE_IN}, {@link #SLIDE_IN_BOTTOM}, {@link #SLIDE_IN_LEFT}, {@link #SLIDE_IN_RIGHT}.
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        switch (animationType) {
            case ALPHA_IN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALE_IN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDE_IN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDE_IN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDE_IN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mSelectAnimation = animation;
    }

    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    /**
     * 设置动画是否只有第一次有效果
     *
     * @param firstOnly true动画只显示一次
     */
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * data binding, {@link BindingAdapter}, load image
     *
     * @param view
     * @param resId
     */
    @BindingAdapter({"imageSrc"})
    public static void loadImage(ImageView view, int resId) {
        view.setImageResource(resId);
    }

}

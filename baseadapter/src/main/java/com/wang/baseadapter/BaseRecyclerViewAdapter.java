
package com.wang.baseadapter;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
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
import java.util.List;

import com.wang.baseadapter.BR;

public abstract class BaseRecyclerViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<BaseViewHolder<T>> {


    private boolean mNextLoadEnable = false;
    private boolean mLoadingMoreEnable = false;
    private boolean mFirstOnlyEnable = true;
    private boolean mOpenAnimationEnable = false;

    protected Context mContext;

    private Interpolator mInterpolator = new LinearInterpolator();
    private int mDuration = 300;
    private int mLastPosition = -1;
    private RequestLoadMoreListener mRequestLoadMoreListener;

    private BaseAnimation mCustomAnimation;
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();

    /**
     * layouts indexed with their types
     */
    private SparseArray<Integer> layouts;

    protected RecyclerViewItemArray mItemArray;

    /**
     * header type
     */
    public static final int HEADER_VIEW = 0x00000111;
    /**
     * loading type
     */
    public static final int LOADING_VIEW = 0x00000222;
    /**
     * footer type
     */
    public static final int FOOTER_VIEW = 0x00000333;
    /**
     * empty type
     */
    public static final int EMPTY_VIEW = 0x00000555;




    @IntDef({ALPHA_IN, SCALE_IN, SLIDE_IN_BOTTOM, SLIDE_IN_LEFT, SLIDE_IN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {

    }

    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHA_IN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALE_IN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDE_IN_RIGHT = 0x00000005;


    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
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


    @SuppressLint("UseSparseArrays")
    public BaseRecyclerViewAdapter(RecyclerViewItemArray itemArray) {
        this.mItemArray = itemArray == null ? new RecyclerViewItemArray() : itemArray;
        layouts = new SparseArray<>();
    }

    /**
     * 添加对应的type和其layout
     * @param type 类型
     * @param layoutResId layout
     */
    @SuppressLint("UseSparseArrays")
    protected void addItemType(int type, int layoutResId) {
        layouts.put(type, layoutResId);
    }

    /**
     * 获取对应的layout
     * @param type 类型
     * @return layoutId
     */
    protected Integer getItemLayout(int type) {
        return layouts.get(type);
    }

    public void remove(int position) {
        mItemArray.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        mItemArray.clear();
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    /**
     * 移除所有的type类型数据
     *
     * @param type 移除的type
     */
    public void removeAllType(int type) {
        if (mItemArray.removeAllType(type) != 0) {
            notifyDataSetChanged();
        }
    }

    /**
     * 移除type类型的第一个数据
     *
     * @param type 类型
     */
    public void removeFirstType(int type) {
        int position = mItemArray.removeFirstType(type);
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    public void add(int position, ItemData item) {
        mItemArray.add(position, item);
        notifyItemInserted(position);
    }

    public void add(ItemData item) {
        if (mItemArray.findLastTypePosition(LOADING_VIEW) != -1
                || mItemArray.findLastTypePosition(FOOTER_VIEW) != -1) {
            mItemArray.add(mItemArray.size() - 1, item);
            notifyItemInserted(mItemArray.size() - 2);
        } else {
            mItemArray.add(item);
            notifyItemInserted(mItemArray.size() - 1);
        }

    }

    public <E> void addAll(int type, List<E> data) {
        if (mItemArray.findLastTypePosition(LOADING_VIEW) != -1
                || mItemArray.findLastTypePosition(FOOTER_VIEW) != -1) {
            for (E e : data) {
                mItemArray.add(mItemArray.size() - 1, new ItemData<>(type, e));
            }
        } else {
            for (E e : data) {
                mItemArray.add(new ItemData<>(type, e));
            }
        }
        notifyDataSetChanged();
    }



    /**
     * 增加到type类型的最后一个
     */
    public void addAfterLast(int type, ItemData data) {
        int position = mItemArray.addAfterLast(type, data);
        if (position != -1) {
            notifyItemInserted(position);
        }
    }

    /**
     * 增加到type类型的第一个
     */
    public void addBeforeFirst(int type, ItemData data) {
        int position = mItemArray.addBeforeFirst(type, data);
        if (position != -1) {
            notifyItemInserted(position);
        }
    }


    /**
     * setting up a new instance to data;
     *
     * @param itemArray
     */
    public void setNewData(RecyclerViewItemArray itemArray) {
        this.mItemArray = itemArray;
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    /**
     * 底部自动加载更多后notify
     * @param type 插入数据类型
     * @param data 插入的数据
     * @param <E> 数据类
     */
    public <E> void notifyDataChangedAfterLoadMore(int type, List<E> data) {
        notifyDataChangedAfterLoadMore(type, data, mNextLoadEnable);
    }

    /**
     * 底部自动加载更多后notify
     * @param type 插入数据类型
     * @param data 插入的数据
     * @param <E> 数据类
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
     * @param isNextLoad 下次滑到底部是否还自动加载
     */
    public void notifyDataChangedAfterLoadMore(boolean isNextLoad) {
        mNextLoadEnable = isNextLoad;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();
    }

    /**
     * 获取列表数据
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
        mContext = parent.getContext();
        BaseViewHolder<T> baseViewHolder;
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = getLoadingView(parent);
                break;
            case HEADER_VIEW:
                baseViewHolder = getHeadView(parent);
                break;
            case EMPTY_VIEW:
                baseViewHolder = getEmptyView(parent);
                break;
            case FOOTER_VIEW:
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
        switch (holder.getItemViewType()) {
            case LOADING_VIEW:
                onBindLoadViewHolder(holder, mItemArray.get(position));
                addLoadMore(holder);
                break;
            case HEADER_VIEW:
                onBindHeadViewHolder(holder, mItemArray.get(position));
                break;
            case EMPTY_VIEW:
                onBindEmptyViewHolder(holder, mItemArray.get(position));
                break;
            case FOOTER_VIEW:
                onBindFooterViewHolder(holder, mItemArray.get(position));
                break;
            default:
                onBindDefViewHolder(holder, mItemArray, position, getItemViewType(position));
                addAnimation(holder);
                break;
        }
    }

    /**
     * 绑定数据到footer
     * @param holder viewHolder
     * @param data 数据
     */
    protected void onBindFooterViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(com.wang.baseadapter.BR.footer, data.getData());
    }

    /**
     * 绑定数据到empty
     * @param holder viewHolder
     * @param data 数据
     */
    protected void onBindEmptyViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.empty, data.getData());
    }

    /**
     * 绑定数据到head
     * @param holder viewHolder
     * @param data 数据
     */
    protected void onBindHeadViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.head, data.getData());
    }

    /**
     * 绑定数据到load
     * @param holder viewHolder
     * @param data 数据
     */
    protected void onBindLoadViewHolder(BaseViewHolder<T> holder, ItemData data) {
        holder.binding.setVariable(BR.load, data.getData());
    }

    /**
     * 绑定数据到自定义视图
     * @param holder viewHolder
     * @param itemArray 数据
     * @param position
     */
    protected abstract void onBindDefViewHolder(BaseViewHolder<T> holder, RecyclerViewItemArray itemArray, int position, int viewType);


    /**
     * 查询数据是否需要binding
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
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
    }


    private BaseViewHolder<T> getEmptyView(ViewGroup parent) {
        Integer layoutId = getItemLayout(EMPTY_VIEW);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_empty_view : layoutId);
    }

    private BaseViewHolder<T> getHeadView(ViewGroup parent) {
        Integer layoutId = getItemLayout(HEADER_VIEW);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_head_view : layoutId);
    }

    private BaseViewHolder<T> getFooterView(ViewGroup parent) {
        Integer layoutId = getItemLayout(FOOTER_VIEW);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_footer_view : layoutId);
    }

    private BaseViewHolder<T> getLoadingView(ViewGroup parent) {
        Integer layoutId = getItemLayout(LOADING_VIEW);
        return BaseViewHolder.create(parent, layoutId == null ? R.layout.def_loading : layoutId);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
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
     * @param holder
     */
    private void addLoadMore(RecyclerView.ViewHolder holder) {
        if (isLoadMore()) {
            mLoadingMoreEnable = true;
            mRequestLoadMoreListener.onLoadMoreRequested();
        }
    }

    protected void afterView(final BaseViewHolder vh, int viewType){

    }

    /**
     * 初始化item接听接口
     * @param vh 对应的viewHolder
     * @param viewType 对应的type
     */
    protected void initItemListener(final BaseViewHolder vh, int viewType) {

    }

    /**
     * 加入并开始动画
     * @param holder 对应的viewHolder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    protected void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * 判断是否需要加载更多
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
        mCustomAnimation = null;
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
        this.mCustomAnimation = animation;
    }

    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    /**
     * 设置动画是否只有第一次有效果
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
     * @param view
     * @param resId
     */
    @BindingAdapter({"imageSrc"})
    public static void loadImage(ImageView view, int resId) {
        view.setImageResource(resId);
    }

}

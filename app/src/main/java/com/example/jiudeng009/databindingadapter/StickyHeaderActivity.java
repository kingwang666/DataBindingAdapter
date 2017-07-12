package com.example.jiudeng009.databindingadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.Toast;

import com.example.jiudeng009.databindingadapter.adapter.StickyHeaderAdapter;
import com.example.jiudeng009.databindingadapter.interfaces.OnRecyclerViewClickListener;
import com.example.jiudeng009.databindingadapter.model.Chapter;
import com.example.jiudeng009.databindingadapter.model.Section;
import com.wang.baseadapter.BaseRecyclerViewAdapter;
import com.wang.baseadapter.util.GravitySnapHelper;
import com.wang.baseadapter.util.SnappingLinearLayoutManager;
import com.wang.baseadapter.StickyHeaderDecoration;
import com.wang.baseadapter.listener.OnHeaderClickListener;
import com.wang.baseadapter.listener.StickyHeaderTouchListener;
import com.wang.baseadapter.model.ItemData;
import com.wang.baseadapter.model.ItemArray;
import com.wang.baseadapter.widget.WaveSideBarView;

/**
 * Created by wang
 * on 2016/11/11
 */

public class StickyHeaderActivity extends AppCompatActivity implements OnRecyclerViewClickListener, OnHeaderClickListener {

    private RecyclerView mRecyclerView;
    private WaveSideBarView mSideBarView;
    private ItemArray mItemArray;
    private boolean move;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSideBarView = (WaveSideBarView) findViewById(R.id.side_view);
        initArray();
        mRecyclerView.setLayoutManager(new SnappingLinearLayoutManager(this));
        StickyHeaderAdapter adapter = new StickyHeaderAdapter(mItemArray, this);
        adapter.openLoadAnimation(BaseRecyclerViewAdapter.SLIDE_IN_LEFT);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerViewListener());
        StickyHeaderDecoration decoration = new StickyHeaderDecoration(StickyHeaderAdapter.TYPE_CHAPTER);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.addOnItemTouchListener(new StickyHeaderTouchListener(this, decoration, this));
        new GravitySnapHelper(Gravity.TOP).attachToRecyclerView(mRecyclerView);
        mSideBarView.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int size = mItemArray.size();
                for (int i = 0; i < size; i++) {
                    ItemData data = mItemArray.get(i);
                    if (data.getDataType() == StickyHeaderAdapter.TYPE_CHAPTER) {
                        Chapter chapter = (Chapter) data.getData();
                        if (chapter.getName().startsWith(letter)) {
                            mRecyclerView.smoothScrollToPosition(i);
                            return;
                        }
                    }
                }
            }
        });
    }


    private void initArray() {
        mItemArray = new ItemArray();
        for (int i = 1; i < 15; i++) {
            Chapter chapter = new Chapter("第" + i + "章", 9);
            mItemArray.add(new ItemData(StickyHeaderAdapter.TYPE_CHAPTER, chapter));
            for (int j = 1; j < 10; j++) {
                Section section = new Section(i + "-" + j);
                chapter.getSections().add(section);
                mItemArray.add(new ItemData(StickyHeaderAdapter.TYPE_SECTION, section));
            }
        }
    }


    @Override
    public void onHeader(int viewType, int position) {
        onChapterClick(position);
    }

    @Override
    public void onClick(int viewType, int position, Object data) {
        switch (viewType) {
            case StickyHeaderAdapter.TYPE_CHAPTER:
                onChapterClick(position);
                break;
            case StickyHeaderAdapter.TYPE_SECTION:
                Section section = (Section) mItemArray.get(position).getData();
                Toast.makeText(StickyHeaderActivity.this, section.name, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onChapterClick(int position) {
        Chapter chapter = (Chapter) mItemArray.get(position).getData();
        Toast.makeText(StickyHeaderActivity.this, chapter.getName(), Toast.LENGTH_SHORT).show();
        if (chapter.isOpen()) {
            chapter.setOpen(false);
//            mRecyclerView.getAdapter().notifyItemChanged(position);
            mItemArray.removeAllAtPosition(position + 1, chapter.getSectionSize());
            mRecyclerView.getAdapter().notifyItemRangeRemoved(position + 1, chapter.getSectionSize());
//            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(position);
//            LinearLayoutManager mLayoutManager =
//                    (LinearLayoutManager) mRecyclerView.getLayoutManager();
//            mLayoutManager.scrollToPositionWithOffset(position, 0);
        } else {
            chapter.setOpen(true);
//            mRecyclerView.getAdapter().notifyItemChanged(position);
            mItemArray.addAllAtPosition(position + 1, StickyHeaderAdapter.TYPE_SECTION, chapter.getSections());
            mRecyclerView.getAdapter().notifyItemRangeInserted(position + 1, chapter.getSectionSize());
//            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                move = false;
                int n = mIndex - ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (0 <= n && n < mRecyclerView.getChildCount()) {
                    int top = mRecyclerView.getChildAt(n).getTop();
                    mRecyclerView.smoothScrollBy(0, top);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }

    }
}

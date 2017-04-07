package com.example.jiudeng009.databindingadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.example.jiudeng009.databindingadapter.adapter.TextAdapter;
import com.example.jiudeng009.databindingadapter.model.TextData;
import com.wang.baseadapter.BaseRecyclerViewAdapter;
import com.wang.baseadapter.model.HeadModel;
import com.wang.baseadapter.model.ItemData;
import com.wang.baseadapter.model.LoadModel;
import com.wang.baseadapter.model.RecyclerViewItemArray;

import java.util.ArrayList;
import java.util.List;

public class MultiTypeActivity extends AppCompatActivity implements BaseRecyclerViewAdapter.RequestLoadMoreListener{

    private RecyclerView recyclerView;
    private RecyclerViewItemArray itemArray;
    private TextAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_type);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        init();
        adapter = new TextAdapter(itemArray);
        adapter.openLoadMore(true);
        adapter.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void init(){
        itemArray = new RecyclerViewItemArray();
        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.TYPE_HEADER, new HeadModel(R.mipmap.jd_l_norecord_icon)));
        for (int i = 0; i < 10; i++){
            itemArray.add(new ItemData<>( i % 2 == 0 ? TextAdapter.TYPE_TEXT_1 : TextAdapter.TYPE_TEXT_2, new TextData(i + "")));
        }
        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.TYPE_LOADING, new LoadModel(false, "loading")));
    }

    @Override
    public void onLoadMoreRequested() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<TextData> datas = new ArrayList<>();
                for (int i = 0; i < 2; i++){
                    datas.add(new TextData(i + 10 + ""));
                }
                if (page == 3){
                    adapter.openLoadMore(false);
                    Log.d("test", "close");
                    LoadModel model = (LoadModel) itemArray.get(itemArray.size() - 1).getData();
                    model.setNoData(true);
                    model.setTip("没有更多数据了");
                }
                adapter.notifyDataChangedAfterLoadMore(TextAdapter.TYPE_TEXT_1, datas);
                page ++;
            }
        }, 1000);
    }
}

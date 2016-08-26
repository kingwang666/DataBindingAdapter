package com.example.jiudeng009.databindingadapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.wang.baseadapter.BaseRecyclerViewAdapter;
import com.wang.baseadapter.model.FooterModel;
import com.wang.baseadapter.model.HeadModel;
import com.wang.baseadapter.model.ItemData;
import com.wang.baseadapter.model.LoadModel;
import com.wang.baseadapter.model.RecyclerViewItemArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseRecyclerViewAdapter.RequestLoadMoreListener{

    private RecyclerView recyclerView;
    private RecyclerViewItemArray itemArray;
    private TextAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        init();
        adapter = new TextAdapter(itemArray);
        adapter.openLoadMore(true);
        adapter.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
//        findViewById(R.id.f_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapter.add(new ItemData<>(0, new TextData("fuck")));
//                ((TextData)itemArray.get(3).getData()).setText(itemArray.size() + "");
//            }
//        });

    }

    private void init(){
        itemArray = new RecyclerViewItemArray();
        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.HEADER_VIEW, new HeadModel(R.mipmap.jd_l_norecord_icon)));
        for (int i = 0; i < 10; i++){
            itemArray.add(new ItemData<>(0, new TextData(i + "")));
        }
//        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.LOADING_VIEW, new LoadModel(false, "正在努力加载数据")));
//        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.EMPTY_VIEW, new EmptyModel(R.mipmap.jd_l_norecord_icon)));
        itemArray.add(new ItemData<>(BaseRecyclerViewAdapter.FOOTER_VIEW, new FooterModel(R.mipmap.ic_launcher, "footer")));
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
                adapter.notifyDataChangedAfterLoadMore(0, datas);
                page ++;
            }
        }, 1000);
    }
}

package com.example.dell.newitsme.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.controller.ActivityCBase;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.LineHotListAdapter;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;
import com.example.model.LiveItemModel;
import java.util.List;
import de.greenrobot.event.EventBus;

public class FragmentHome extends Fragment {

    private static final String TAG = "FragmentHome";
    private ListView mListView;
    private TextView mTextView;
    private LineHotListAdapter _hotListAdapter;
    private ActivityCBase _activityCBase;

    public FragmentHome() {
    }

    public void init(ActivityCBase activityCBase){
        _activityCBase = activityCBase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_home, container, false);

        mListView = (ListView)root.findViewById(R.id.listviw);
        mTextView =  (TextView) root.findViewById(R.id.tv_loading);
        _hotListAdapter = new LineHotListAdapter(_activityCBase);
        mListView.setAdapter(_hotListAdapter);

        //这里是接收事件类的注册事件
        EventBus.getDefault().register(this);

        _hotListAdapter.requestHot();
        return root;
    }

    /*如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，
    onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行*/
    public void onEventMainThread(TurtleEvent event) {
        int type = event.getType();
        if(type == TurtleEventType.TYPE_HOT_DATA_OK ){
            List<LiveItemModel> datas = event.getParam();
            if (datas == null)return;
            _hotListAdapter.setData(datas);//把获得的数据传给Adapter

            mListView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            Log.i(TAG, "TYPE_HOT_DATA_OK");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//当这个类销毁的时候解除注册
    }

    //

    //

    //


}

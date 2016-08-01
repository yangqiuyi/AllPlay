package com.example.dell.newitsme.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.LineHotListAdapter;

public class FragmentHome extends Fragment {

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_home, container, false);

        mListView = (ListView)root.findViewById(R.id.listviw);
        LineHotListAdapter adapter = new LineHotListAdapter(getActivity());
        mListView.setAdapter(adapter);

        adapter.requestHot();
        return root;
    }

}

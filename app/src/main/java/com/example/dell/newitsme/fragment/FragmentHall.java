package com.example.dell.newitsme.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.ViewPagerAdapter;
import com.example.dell.newitsme.controller.ActivityCBase;


public class FragmentHall  extends Fragment{
    private ViewPager mViewPager;
    private FragmentFoucs mFragmentFoucs;
    private FragmentNew mFragmentNew;
    private FragmentHot mFragmentHot;
    private TabLayout mTabLayout;
    private  ViewPagerAdapter  mViewPagerAdapter;
    private ActivityCBase mActivityCBase;

    public void initParam(ActivityCBase activityCBase){
        mActivityCBase = activityCBase;//传参数，因为热门列表的适配器需要ActivityCBase；这个方法会在mainActivity被调用，传一个ActivityCBase进来
    }                                    // mFragmentHot.init(mActivityCBase);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_hall, container, false);
        initTabsAndViewPager(root);
        return root;
    }

   private  void initTabsAndViewPager(View parent){
       //
       mViewPager = (ViewPager)parent.findViewById(R.id.viewpager);
       mTabLayout =(TabLayout) parent.findViewById(R.id.tablayout);
       //
       mFragmentFoucs = new FragmentFoucs();
       mFragmentNew = new FragmentNew();
       mFragmentHot = new FragmentHot();
       mFragmentHot.init(mActivityCBase);
       mFragmentFoucs.init(mActivityCBase);

       mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
       mViewPagerAdapter.addFragment(mFragmentFoucs,"关注");
       mViewPagerAdapter.addFragment(mFragmentHot,"热门");
       mViewPagerAdapter.addFragment(mFragmentNew,"最新");

       mViewPager.setAdapter(mViewPagerAdapter);

       mTabLayout.setupWithViewPager(mViewPager);
       mTabLayout.setTabMode(TabLayout.MODE_FIXED);


   }



}

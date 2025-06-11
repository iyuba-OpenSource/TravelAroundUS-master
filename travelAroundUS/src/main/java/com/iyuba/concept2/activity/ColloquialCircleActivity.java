package com.iyuba.concept2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.FragmentAdapter;
import com.iyuba.concept2.fragment.MyColloquialCircleFragment;
import com.iyuba.concept2.fragment.NewColloquialCircleFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 口语圈
 */
public class ColloquialCircleActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private ViewPager2 viewPager;

    private Button btnBack;

    private FragmentAdapter fragmentAdapter = new FragmentAdapter(this,new ArrayList<>(
            Arrays.asList(new NewColloquialCircleFragment(),new MyColloquialCircleFragment())
    ));

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,ColloquialCircleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colloquial_circle);
        initView();
    }

    private void initView(){
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((View v)->{
            finish();
        });
        viewPager.setAdapter(fragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if(position == 0){
                tab.setText("最新口语");
            }else{
                tab.setText("我的口语");
            }
        }).attach();
    }
}
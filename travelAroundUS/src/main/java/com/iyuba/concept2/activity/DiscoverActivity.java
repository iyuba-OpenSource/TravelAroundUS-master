//package com.iyuba.concept2.activity;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//
//import android.os.Bundle;
//
//import com.iyuba.concept2.R;
//import com.iyuba.concept2.fragment.DiscoverFragment;
//
//public class DiscoverActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_discover);
//        FragmentManager supportFragmentManager = getSupportFragmentManager();
//        DiscoverFragment discoverFragment = new DiscoverFragment();
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,discoverFragment).commit();
//    }
//}
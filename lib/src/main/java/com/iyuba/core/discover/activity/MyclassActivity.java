//package com.iyuba.core.discover.activity;
//
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.iyuba.core.microclass.fragment.MobClassListFragment;
//import com.iyuba.lib.R;
//
//public class MyclassActivity extends AppCompatActivity {
//
//    private FragmentManager fragmentManager;
//    private MobClassListFragment microClassListFragment;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_myclass);
//        fragmentManager = this.getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        if (microClassListFragment == null) {
//            microClassListFragment = new MobClassListFragment();
//            transaction.add(R.id.fragment, microClassListFragment);
//        } else {
//            transaction.show(microClassListFragment);
//        }
//        transaction.commit();
//    }
//}

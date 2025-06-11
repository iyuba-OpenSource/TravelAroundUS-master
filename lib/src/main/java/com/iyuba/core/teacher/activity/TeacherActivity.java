package com.iyuba.core.teacher.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.core.teacher.fragment.TeacherFragment;
import com.iyuba.lib.R;

/**
 * Created by ivotsm on 2017/3/28.
 */

public class TeacherActivity extends FragmentActivity {
    private TeacherFragment teacherFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_empty);

        teacherFragment = new TeacherFragment();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment,teacherFragment);
        ft.commit();
    }
}

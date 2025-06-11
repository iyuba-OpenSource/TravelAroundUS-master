package com.iyuba.core.lil.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.core.lil.util.LibStackUtil;

/**
 * @title: 基础的堆栈activity
 * @date: 2023/11/29 11:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseStackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LibStackUtil.getInstance().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LibStackUtil.getInstance().remove(this);
    }
}

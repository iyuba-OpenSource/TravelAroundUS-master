package com.iyuba.core.lil.ui.base;

import android.app.ActivityGroup;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.iyuba.core.lil.util.LibStackUtil;

/**
 * @title:
 * @date: 2023/12/15 16:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseStudyStackActivity extends ActivityGroup {

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

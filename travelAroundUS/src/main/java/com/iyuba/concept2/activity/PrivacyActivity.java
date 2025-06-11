package com.iyuba.concept2.activity;

import android.content.Intent;
import android.os.Bundle;

import com.iyuba.concept2.R;
import com.iyuba.core.common.util.CommonUtils;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.mob.MobSDK;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;

public class PrivacyActivity extends BaseStackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        if (ConfigManager.Instance().loadInt("version")<Constant.GUIDE_VERSION) {
            //还未同意
            PrivacyDialog.showDialog(this, () -> initAndTurn());
        }else {
            //已同意
            initAndTurn();
        }
    }

    //application初始化，并且跳转
    private void initAndTurn(){
        ConceptApplication.getInstance().init();

        MobSDK.submitPolicyGrantResult(true);
        String channel = ChannelReaderUtil.getChannel(this);
        UMConfigure.init(getApplicationContext(), Constant.UM_KEY, channel,UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(false);

        PrivacyInfoHelper.init(getApplicationContext());
        PrivacyInfoHelper.getInstance().putApproved(true);

        IPrivacy.init(getApplicationContext(),
                CommonUtils.getUserAgreementUrl(this),
                CommonUtils.getPrivacyPolicyUrl(this));

        YdConfig.getInstance().init(this,Constant.APPID);

        startActivity(new Intent(this,WelcomeActivity.class));
        this.finish();
    }
}
package com.iyuba.concept2.lil.manager;

import com.iyuba.concept2.lil.HelpUtil;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.OtherApi;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.lil.bean.AppCheckResponse;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @title: 审核控制管理
 * @date: 2023/6/26 13:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class VerifyManager {

    private static VerifyManager instance;

    public static VerifyManager getInstance(){
        if (instance==null){
            synchronized (VerifyManager.class){
                if (instance==null){
                    instance = new VerifyManager();
                }
            }
        }
        return instance;
    }

    //微课审核
    public void verifyMoc(){
        //根据渠道获取id
        String channel = ChannelReaderUtil.getChannel(ConceptApplication.getContext());
        int mocId = Constant.getMocLimitChannelId(channel);

        String version = HelpUtil.getAppVersion(ConceptApplication.getContext(),ConceptApplication.getContext().getPackageName());
        if (HelpUtil.isBelongToOppoPhone()){
            version = "oppo_"+version;
        }

        ApiRequestFactory.getOtherApi().appVerify(OtherApi.APP_VERIFY_URL,mocId,version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitMoc(false);
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")){
                            AbilityControlManager.getInstance().setLimitMoc(false);
                        }else {
                            AbilityControlManager.getInstance().setLimitMoc(true);
                        }
                    }
                });
    }

    //视频审核
    public void verifyVideo(){
        //根据渠道获取id
        String channel = ChannelReaderUtil.getChannel(ConceptApplication.getContext());
        int mocId = Constant.getVideoLimitChannelId(channel);

        String version = HelpUtil.getAppVersion(ConceptApplication.getContext(),ConceptApplication.getContext().getPackageName());
        if (HelpUtil.isBelongToOppoPhone()){
            version = "oppo_"+version;
        }

        ApiRequestFactory.getOtherApi().appVerify(OtherApi.APP_VERIFY_URL,mocId,version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitVideo(false);
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")){
                            AbilityControlManager.getInstance().setLimitVideo(false);
                        }else {
                            AbilityControlManager.getInstance().setLimitVideo(true);
                        }
                    }
                });
    }
}

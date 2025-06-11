package com.iyuba.concept2.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.ColloquialCircleAdapter;
import com.iyuba.concept2.api.RetrofitFactory;
import com.iyuba.concept2.api.data.EvaluateRecordResponse;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyColloquialCircleFragment extends Fragment {

    public ColloquialCircleAdapter adapter = new ColloquialCircleAdapter();

    private RecyclerView recyclerView;

    private String currentVoiceUri = "";
    private ImageView lastImageView;

    //2.12版本以后推荐
    ExoPlayer player;

    public MyColloquialCircleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_colloquial_circle, container, false);
        player = new ExoPlayer.Builder(getActivity()).build();
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initView(View container) {
        recyclerView = container.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnSoundClickListener((bean, img) -> {
            initPlayer(bean,img);
        });
        adapter.setOnLongClickListener(bean->{
            deleteColloquial(bean.getId());
        });
    }

    private void initData(){
        getData();
    }

    private void deleteColloquial(String id){
        Map map = new HashMap<String, String>(){{
            put("protocol", "61003");
            put("id", id);
        }};
        RetrofitFactory.getSpeechApi().listEvaluateRecord(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<EvaluateRecordResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.e("TAG", "onCompleted: wancehng" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "onError: 出错"+e.getMessage());
                    }

                    @Override
                    public void onNext(EvaluateRecordResponse evaluateRecordResponse) {
                        ToastUtil.showToast(getActivity(),"删除成功");
                        getData();
                    }

                });
    }

    public void getData() {
        Map map = new HashMap<String, String>(){{
            put("protocol", "60001");
            put("topic", Constant.APPType);
            put("pageNumber", "1");
            put("pageCounts", "100");
            put("appid", Constant.APPID);
            put("userid", String.valueOf(UserInfoManager.getInstance().getUserId()));
            put("selflg", "1");
        }};
        RetrofitFactory.getSpeechApi().listEvaluateRecord(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<EvaluateRecordResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.e("TAG", "onCompleted: wancehng" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "onError: 出错"+e.getMessage());
                    }

                    @Override
                    public void onNext(EvaluateRecordResponse evaluateRecordResponse) {
                        adapter.setData(evaluateRecordResponse);
                        adapter.notifyDataSetChanged();
                        Log.e("TAG", "onNext: "+evaluateRecordResponse.toString());
                    }

                });
    }

    private void initPlayer(EvaluateRecordResponse.Data bean,ImageView imageView){
        if(player.isPlaying()){
            if(currentVoiceUri == bean.getVideoUrl()){
                player.pause();
                showAni(false,imageView);
                return;
            }
        }
        if(lastImageView != null){
            showAni(false,lastImageView);
        }
        lastImageView = imageView;
        currentVoiceUri = bean.getVideoUrl();
        Log.e("TAG", "initPlayer: "+"http://iuserspeech.iyuba.cn:9001/voa/"+bean.getShuoShuo());
        MediaItem item = MediaItem.fromUri(Uri.parse("http://iuserspeech.iyuba.cn:9001/voa/"+bean.getShuoShuo()));
        //设置ExoPlayer需要播放的多媒体item
        player.setMediaItem(item);
        //设置播放器是否当装备好就播放， 如果看源码可以看出，ExoPlayer的play()方法也是调用的这个方法
        player.setPlayWhenReady(true);
        player.prepare();
        showAni(true,imageView);
    }

    private void showAni(Boolean show,ImageView imageView){
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        if(show){
            drawable.start();
        }else{
            drawable.stop();
            drawable.selectDrawable(0);
        }
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.pause();
        player.release();
    }
}

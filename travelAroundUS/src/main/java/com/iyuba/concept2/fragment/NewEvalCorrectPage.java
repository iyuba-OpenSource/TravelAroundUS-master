package com.iyuba.concept2.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.iyuba.concept2.R;
import com.iyuba.concept2.lil.manager.AbilityControlManager;
import com.iyuba.concept2.manager.RecordManager;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.concept2.util.ResultParse;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.OtherApi;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.common.retrofitapi.result.WordEvalResult;
import com.iyuba.core.common.retrofitapi.result.WordExplainResult;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.util.WordPlayer;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.search.bean.EvaluationBeanNew;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *  新的纠音界面--主要是更换了数据显示
 * <p>
 * 这里有两个解决方案
 * 1.传入一些解析好的数据，然后进行展示和处理
 * 2.将前面的spanstringbuilder传入并直接展示
 */
public class NewEvalCorrectPage extends DialogFragment {
    private static final String TAG = "EvalCorrectPage";

    private View root;

    private TextView tvTitle, tvSentence, tvRight, tvYou, tvExplain, tvRecordText, tvScore;
    private ImageView ivClose, ivWordSpeak, ivRecord;
    private RelativeLayout lisaLayout;
    private LinearLayout llSpeak, llRecord, llRead;

    //课文数据
    private VoaDetail voaDetail;
    //单词评测数据
    private EvalSentenceEntity evaluateBean;

    //用户id数据
    private int uid = 0;
    //单词id
    private int wordId = 0;
    //单词的分数
    private double wordScore = 0;
    //单词的音标
    private String wordPorn;
    //录音的音标
    private String userPorn;
    //标题上显示的单词
    private String titleWord;

    //错误单词分数标准线
    private double errorWordScore = ResultParse.errorWordScore;
    //正确单词分数标准线(这里要求不增加正确的读音，将正确的标准线拉到10，原来是4)
    private double rightWordScore = ResultParse.rightWordScore;

    //单词释义的数据
    private WordExplainResult explainResult;
    //评测的数据
    private WordEvalResult evalResult;

    //是否正在录音
    private boolean isRecording = false;
    //是否正在评测
    private boolean isEvaluating = false;
    //单词播放器
    private WordPlayer player;
    //录音管理器
    private RecordManager recordManager;
    //录音文件
    private File recordFile;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_eval_correct, null);

        tvTitle = root.findViewById(R.id.correct_title);
        tvSentence = root.findViewById(R.id.correct_sentence);
        tvRight = root.findViewById(R.id.correct_oral);
        tvYou = root.findViewById(R.id.self_oral);
        tvExplain = root.findViewById(R.id.word_define);
        tvRecordText = root.findViewById(R.id.eval_hint);
        tvScore = root.findViewById(R.id.word_score);

        ivWordSpeak = root.findViewById(R.id.img_speaker);
        ivClose = root.findViewById(R.id.correct_close);
        ivRecord = root.findViewById(R.id.eval_speaker);

        lisaLayout = root.findViewById(R.id.lisa_layout);

        llSpeak = root.findViewById(R.id.ll_reader);
        llRecord = root.findViewById(R.id.ll_eval);
        llRead = root.findViewById(R.id.ll_score);

        setClick();

        builder.setView(root);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getDialog() != null) {
            getDialog().dismiss();
        }

        handler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.ReleasePlayer();
        }
        if (recordManager != null) {
            recordManager.stopRecord();
        }
        if (loadWordExplain != null) {
            loadWordExplain.unsubscribe();
        }
        if (uploadEval != null) {
            uploadEval.unsubscribe();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        showData();
        setUIData(userPorn, wordPorn);

        player = new WordPlayer();
        player.initMediaplayer();
    }

    //设置点击
    private void setClick() {
        ivClose.setOnClickListener(onClickListener);
        ivWordSpeak.setOnClickListener(onClickListener);
        llSpeak.setOnClickListener(onClickListener);
        ivRecord.setOnClickListener(onClickListener);
        llRead.setOnClickListener(onClickListener);

        llRead.setVisibility(View.INVISIBLE);
        if (!AbilityControlManager.getInstance().isLimitMoc()) {
            lisaLayout.setVisibility(View.VISIBLE);
            lisaLayout.setOnClickListener(onClickListener);
        } else {
            lisaLayout.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.correct_close:
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }

                    handler.removeCallbacksAndMessages(null);
                    if (player != null) {
                        player.ReleasePlayer();
                    }
                    if (recordManager != null) {
                        recordManager.stopRecord();
                    }
                    if (loadWordExplain != null) {
                        loadWordExplain.unsubscribe();
                    }
                    if (uploadEval != null) {
                        uploadEval.unsubscribe();
                    }
                    break;
                case R.id.img_speaker:
                case R.id.ll_reader:
                    //读单词和读原音
                    if (isRecording) {
                        ToastUtil.showToast(getActivity(), "录音中...");
                        return;
                    }

                    if (isEvaluating) {
                        ToastUtil.showToast(getActivity(), "评测中...");
                        return;
                    }

                    if (explainResult != null && !TextUtils.isEmpty(explainResult.getAudio())) {
                        player.stopPlayer();
                        player.playMusic(explainResult.getAudio());
                    } else {
                        ToastUtil.showToast(getActivity(), "未查询到该单词的读音");
                    }
                    break;
                case R.id.eval_speaker:
                    //评测
                    if (player != null) {
                        player.stopPlayer();
                    }

                    if (isRecording) {
                        stopRecord(true);
                        uploadEvalData();
                    } else {
                        startRecord();
                    }
                    break;
                case R.id.ll_score:
                    //自己读音
                    if (isRecording) {
                        ToastUtil.showToast(getActivity(), "录音中...");
                        return;
                    }

                    if (isEvaluating) {
                        ToastUtil.showToast(getActivity(), "评测中...");
                        return;
                    }

                    if (player != null) {
                        player.stopPlayer();
                    }

                    if (recordFile != null) {
                        player.playMusic(recordFile.getAbsolutePath());
                    } else {
                        ToastUtil.showToast(getActivity(), "当前没有评测单词");
                    }
                    break;
                case R.id.lisa_layout:
                    ArrayList<Integer> typeIdFilter = new ArrayList<>();
                    typeIdFilter.add(3);
                    startActivity(MobClassActivity.buildIntent(getActivity(), 3, true, typeIdFilter));
                    break;
            }
        }
    };

    //设置uid
    public void setUid(int uid) {
        this.uid = uid;
    }

    //设置单词数据
    public void setEvaluateBean(EvalSentenceEntity bean) {
        this.evaluateBean = bean;
        List<Eval_result.WordsBean> wordList = GsonUtils.toObjectList(bean.evalWords,Eval_result.WordsBean.class);

        //获取第一个数据显示
        if (wordList != null && wordList.size() > 0) {
            int flag = 0;
            for (int i = 0; i < wordList.size(); i++) {
                double score = Double.parseDouble(wordList.get(i).getScore());
                if (flag == 0 && (score < errorWordScore || score > rightWordScore)) {
                    flag++;
                    titleWord = wordList.get(i).getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                    wordScore = score;
                    wordId = Integer.parseInt(wordList.get(i).getIndex());

                    userPorn = wordList.get(i).getUser_pron2();
                    wordPorn = wordList.get(i).getPron2();
                }
            }
        }
    }

    //设置课文数据
    public void setVoaDetail(VoaDetail detail) {
        this.voaDetail = detail;
    }

    //显示数据
    private void showData() {
        List<Eval_result.WordsBean> showWordList = GsonUtils.toObjectList(evaluateBean.evalWords,Eval_result.WordsBean.class);

        if (evaluateBean != null
                && showWordList != null
                && showWordList.size() > 0) {

            int clickCount = 0;
            for (int i = 0; i < showWordList.size(); i++) {
                Eval_result.WordsBean wordsBean = showWordList.get(i);
                if (wordsBean != null
                        && !TextUtils.isEmpty(wordsBean.getContent())
                        && !("-").equals(wordsBean.getContent())
                        && !("—").equals(wordsBean.getContent())
                        && ((Double.parseDouble(wordsBean.getScore()) < errorWordScore) || (Double.parseDouble(wordsBean.getScore()) > rightWordScore))) {
                    //这里计算的为正确和错误单词都显示

                    if (Double.parseDouble(wordsBean.getScore()) < errorWordScore) {
                        clickCount++;
                    }

                    if (Double.parseDouble(wordsBean.getScore()) > rightWordScore) {
                        clickCount++;
                    }
                }
            }

//            if (clickCount==1){
//                tvSentence.setText(ResultParse.setSenResultEval(wordList));
//            }else
            if (clickCount > 0) {
                ClickableSpan[] clickableSpans = new ClickableSpan[clickCount];
                SpannableStringBuilder span = new SpannableStringBuilder(evaluateBean.sentence);
                int spIndex = 0;

                for (int i = 0; i < showWordList.size(); i++) {
                    Eval_result.WordsBean tempWord = showWordList.get(i);
                    int curIndex = i;

                    if (tempWord != null
                            && !TextUtils.isEmpty(tempWord.getContent())
                            && !("-").equals(tempWord.getContent())
                            && !("—").equals(tempWord.getContent())
                            && ((Double.parseDouble(tempWord.getScore()) < errorWordScore) || (Double.parseDouble(tempWord.getScore()) > rightWordScore))) {
                        //标明正确和错误的单词
                        if (Double.parseDouble(tempWord.getScore()) < errorWordScore) {
                            tempWord.setContent(tempWord.getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1"));
                        }

                        if (Double.parseDouble(tempWord.getScore()) > rightWordScore) {
                            tempWord.setContent(tempWord.getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1"));
                        }

                        clickableSpans[spIndex] = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                if (tempWord != null
                                        && Integer.parseInt(tempWord.getIndex()) == curIndex) {
                                    if (wordId != Integer.parseInt(tempWord.getIndex())) {
                                        wordId = Integer.parseInt(tempWord.getIndex());
                                        wordScore = Double.parseDouble(tempWord.getScore());
                                        titleWord = tempWord.getContent();
                                        //设置显示
                                        setUIData(tempWord.getUser_pron2(), tempWord.getPron2());
                                        llRead.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                if (Double.parseDouble(tempWord.getScore()) < errorWordScore) {
                                    ds.setColor(Color.RED);
                                } else if (Double.parseDouble(tempWord.getScore()) > rightWordScore) {
                                    ds.setColor(0xff079500);
                                }
                            }
                        };

                        //获取单词匹配不到时的大约位置
                        int index = evaluateBean.sentence.indexOf(tempWord.getContent());
                        if (index < 0) {
                            if (tempWord.getContent().contains("’")) {
                                index = evaluateBean.sentence.indexOf(tempWord.getContent().replace("’", "'"));
                            } else if (tempWord.getContent().contains("'")) {
                                index = evaluateBean.sentence.indexOf(tempWord.getContent().replace("'", "’"));
                            } else {
                                index = 0;
                            }
                        }

                        //获取单词在句子中的位置
                        String[] wordList = evaluateBean.sentence.split(" ");
                        int start = 0;
                        for (String word : wordList) {
                            if (TextUtils.isEmpty(word)) {
                                //这里因为中间有空格，会导致数据显示不正确
                                start++;
                                continue;
                            }

                            String wordRl = word.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                            if (word.equalsIgnoreCase(tempWord.getContent())) {
                                index = start;
                                break;
                            } else if (wordRl.equalsIgnoreCase(tempWord.getContent())) {
                                index = start + word.indexOf(wordRl);
                                break;
                            }

                            start += word.length();
                            ++start;
                        }

                        span.setSpan(clickableSpans[spIndex], index, index + tempWord.getContent().length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
                        ++spIndex;
                    }
                    //设置
                    tvSentence.setText(span);
                    tvSentence.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }
    }

    //设置ui显示
    private void setUIData(String userPorn, String wordPorn) {
        tvTitle.setText(titleWord);
        String wordText = getResources().getString(R.string.eval_ok_sound) + titleWord;
        if (!TextUtils.isEmpty(wordPorn)) {
            wordText += "[" + wordPorn + "]";
        }
        tvRight.setText(wordText);
        String userText = getResources().getString(R.string.eval_self_sound) + titleWord;
        if (!TextUtils.isEmpty(userPorn)) {
            userText += "[" + userPorn + "]";
        }
        tvYou.setText(userText);
        tvScore.setText((int) (wordScore * 20) + "");
        if (!TextUtils.isEmpty(titleWord)) {
            //加载释义
            loadWordExplain();
        }
    }

    //加载单词释义
    private Subscription loadWordExplain;

    private void loadWordExplain() {
        if (loadWordExplain != null) {
            loadWordExplain.unsubscribe();
        }
        loadWordExplain = ApiRequestFactory.getOtherApi().getWordExplain(OtherApi.WORD_EXPLAIN_URL, titleWord,
                "",
                "",
                Integer.parseInt(Constant.APPID),
                UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WordExplainResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: 获取单词释义失败");
                    }

                    @Override
                    public void onNext(WordExplainResult wordExplainResult) {
                        if (wordExplainResult != null) {
                            explainResult = wordExplainResult;
                            handler.sendEmptyMessage(1);
                            return;
                        }
                        handler.sendEmptyMessage(0);
                    }
                });
    }

    //提交评测数据
    private Subscription uploadEval;

    private void uploadEvalData() {
        if (uploadEval != null) {
            uploadEval.unsubscribe();
        }

        Map<String, RequestBody> map = new HashMap<>();
        map.put(OtherApi.WordEval.Param.Key.SENTENCE, transToRequestBody(titleWord));
        map.put(OtherApi.WordEval.Param.Key.IDINDEX, transToRequestBody("1"));
        map.put(OtherApi.WordEval.Param.Key.PARAID, transToRequestBody(voaDetail.paraId));
        map.put(OtherApi.WordEval.Param.Key.NEWSID, transToRequestBody(String.valueOf(voaDetail.voaId)));
        map.put(OtherApi.WordEval.Param.Key.USERID, transToRequestBody(String.valueOf(UserInfoManager.getInstance().getUserId())));
        map.put(OtherApi.WordEval.Param.Key.TYPE, transToRequestBody(Constant.APPType));
        map.put("appId", transToRequestBody(Constant.APPID));
        map.put("wordId", transToRequestBody(String.valueOf(wordId)));
        map.put("flg", transToRequestBody("2"));

        uploadEval = ApiRequestFactory.getOtherApi().uploadWordEval(OtherApi.WORD_EVAL_URL, map, transToPart(recordFile))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WordEvalResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onNext(WordEvalResult wordEvalResult) {
                        if (wordEvalResult != null && wordEvalResult.getData() != null) {
                            evalResult = wordEvalResult;
                            Message message = new Message();
                            message.what = 3;
                            message.obj = wordEvalResult;
                            handler.sendMessage(message);
                            return;
                        }
                        handler.sendEmptyMessage(2);
                    }
                });
    }

    //将string数据包装成RequestBody
    private RequestBody transToRequestBody(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }

    private MultipartBody.Part transToPart(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    //获取单词释义失败
                    tvExplain.setText("获取单词释义失败");
                    break;
                case 1:
                    //获取单词释义
                    if (explainResult != null) {
                        tvExplain.setText(getResources().getString(R.string.eval_word_dict) + (TextUtils.isEmpty(explainResult.getDef()) ? "暂无释义" : explainResult.getDef()));
                        if (!TextUtils.isEmpty(explainResult.getPron())) {
                            tvRight.setText(getResources().getString(R.string.eval_ok_sound) + titleWord + "[" + explainResult.getPron() + "]");
                        }
                    }

                    if (explainResult != null && !TextUtils.isEmpty(explainResult.getAudio())) {
                        llSpeak.setVisibility(View.VISIBLE);
                        ivWordSpeak.setVisibility(View.VISIBLE);
                    } else {
                        llSpeak.setVisibility(View.INVISIBLE);
                        ivWordSpeak.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 2:
                    //评测失败
                    isEvaluating = false;
                    ToastUtil.showToast(getActivity(), "评测提交失败，请重试");

                    tvRecordText.setText("点击录音");
                    ivRecord.setImageResource(R.drawable.sen_eval_mic);
                    break;
                case 3:
                    //评测成功
                    isEvaluating = false;
                    tvRecordText.setText("点击录音");
                    ivRecord.setImageResource(R.drawable.sen_eval_mic);
                    ToastUtil.showToast(getActivity(), "评测成功");

                    if (msg.obj != null) {
                        WordEvalResult evalResult = (WordEvalResult) msg.obj;
                        if (evalResult != null) {
                            try {
                                llRead.setVisibility(View.VISIBLE);
                                wordScore = Double.parseDouble(evalResult.getData().getTotal_score());
                                tvScore.setText((int) (wordScore * 20) + "");
                                if (evalResult.getData().getWords() != null && evalResult.getData().getWords().size() > 0) {
                                    userPorn = evalResult.getData().getWords().get(0).user_pron2;
                                    if (TextUtils.isEmpty(userPorn) || "null".equalsIgnoreCase(userPorn)) {
                                        userPorn = "";
                                    }
                                    tvYou.setText(getResources().getString(R.string.eval_self_sound) + titleWord + "[" + userPorn + "]");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //开启录音
    private void startRecord() {
        isRecording = true;
        //这里创建文件，保存在外部存储
        recordFile = new File(getActivity().getExternalFilesDir(null).getPath() + "/record/record_eval_" + System.currentTimeMillis() + ".mp3");
        try {
            if (recordFile.exists()) {
                recordFile.delete();
            } else {
                if (!recordFile.getParentFile().exists()) {
                    recordFile.getParentFile().mkdirs();
                }
                recordFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvRecordText.setText("点击停止");
        recordManager = new RecordManager(recordFile);
        recordManager.startRecord();
    }

    //结束录音
    private void stopRecord(boolean isNextEval) {
        isRecording = false;
        isEvaluating = isNextEval;
        if (recordManager != null) {
            recordManager.stopRecord();
        }

        if (isNextEval) {
            tvRecordText.setText("评测中");
            LibGlide3Util.loadGif(getActivity(),R.drawable.ic_loading,0,ivRecord);
        } else {
            tvRecordText.setText("点击录音");
        }
    }
}

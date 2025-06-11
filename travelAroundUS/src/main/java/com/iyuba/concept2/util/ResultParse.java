package com.iyuba.concept2.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.facebook.stetho.common.LogUtil;
import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.entity.Sentence;
import com.iflytek.ise.result.entity.Word;
import com.iyuba.core.search.bean.EvaluatBean;
import com.iyuba.core.search.bean.EvaluationBeanNew;

import java.util.ArrayList;
import java.util.List;

public class ResultParse {
    private static final String TAG = ResultParse.class.getSimpleName();

    private static int parseIndex;
    private static SpannableStringBuilder spannable;
    private static String sen;

    public static SpannableStringBuilder getSenResult(Result result, String s) {
       // Log.e(TAG, "The total score of the result is " + result.total_score);
        sen = s;
       // Log.e(TAG, sen);
        spannable = new SpannableStringBuilder(sen);
        parseIndex = 0;

        for (Sentence sentence : result.sentences) {
           // Log.e(TAG, "sentence content is " + sentence.content);
           // Log.e(TAG, "sentence total_score is " + sentence.total_score);
            if (!sentence.content.equals("sil")) {
                for (Word word : sentence.words) {
                   // Log.e(TAG, "Word: " + word.content + " Score: " + word.total_score);
                    if (word.total_score != 0) {
                        setWord(word);
                    }
                }
            }
        }

        return spannable;
    }


    public static SpannableStringBuilder getSenResultLocal(String[] style, String s) {
        sen = s;
        //Log.e(TAG, sen);
        spannable = new SpannableStringBuilder(sen);
        parseIndex = 0;


        String[] words = s.split(" ");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {

            if (!"".equals(words[i])) {
                stringList.add(words[i]);
            }
        }
        LogUtil.e("stringList " + stringList + " " + stringList.size());
        LogUtil.e("stringList style[i]" + style + " " + style.length);
        for (int j = 0; j < stringList.size(); j++) {
            Word word = new Word();
            word.content = stringList.get(j);
            if (style.length>=j) {
                //数组越界 本地数据带有空格
                word.total_score = Float.parseFloat(style[style.length-1]);
            }else {
                word.total_score = Float.parseFloat(style[j]);
            }

            try {
                setWord(word);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("stringList " + e);
            }
        }


        return spannable;
    }

    public static void setWord(Word word) {
        String wordStr = word.content;
        // wordStr=wordStr.replaceAll("\\p{P}", "");
       // LogUtil.e(TAG, "Setting Word : " + wordStr);
        if (word.total_score < 3 || word.total_score > 4) {
            int start = -1;
            int end = -1;
            int i = parseIndex;
            ///LogUtil.e(TAG, "pareseIndex is : " + parseIndex);
            for (int j = 0; i < sen.length() && j < wordStr.length(); i++) {
               // LogUtil.e(TAG, sen.charAt(i) + "___" + wordStr.charAt(j));
                if (start == -1) {
                    if (sen.charAt(i) == wordStr.charAt(j)
                            || sen.charAt(i) + 32 == wordStr.charAt(j)) {
                        start = i;
                        j++;
                    }
                } else {
                    if (sen.charAt(i) == wordStr.charAt(j)) {
                        j++;
                    } else {
                        return;
                    }
                }
            }


            if (start != -1) {
                end = i;
                parseIndex = i;
                //LogUtil.e("单词*******" + wordStr + "+start : " + start + " end : " + end);
                try {
                    String s_start = wordStr.substring(0, 1);
                    // String s_end = wordStr.substring(wordStr.length() - 1,wordStr.length());
                    //
                    String s_end = wordStr.substring(wordStr.length() - 1);
                    //LogUtil.e("单词==" + wordStr + "开头" + s_start + "结尾" + s_end + "***");
                    //LogUtil.e("单词 " + wordStr + "分数" + word.total_score);
                    if (check(s_start)) {
                        start = start + 1;
                    }
                    if (check(s_end) || s_end.equals("")||s_end.length()==0 ) {
                        end = end - 1;
                    }else if (s_end.indexOf('\r')>=0){//判断是否包含转义字符
                        end = end - 2;
                    }
                } catch (Exception e) {
                    LogUtil.e("异常" + e);
                }

                if (word.total_score <=(float) 2.5) {
                    setRed(start, end);

                } else if (word.total_score >=(float) 4) {
                    try {
                        setGreen(start, end);
                    } catch (Exception e) {
                        LogUtil.e("异常" + e);
                    }
                }
            }
        } else {
            parseIndex += wordStr.length() + 1;
        }
        while (parseIndex < sen.length() && !isAlphabeta(sen.charAt(parseIndex))) {
            parseIndex++;
        }

    }

    private static boolean isAlphabeta(char c) {
        boolean result = false;
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            result = true;
        return result;
    }

    public static void setRed(int start, int end) {
//        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    public static void setGreen(int start, int end) {
        spannable.setSpan(new ForegroundColorSpan(0xff079500), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     *
     * @param s
     * @return
     */
    public static boolean check(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }

        return b;
    }

    //标记单词在句子中的位置
    public static SpannableStringBuilder setSenResultEval(List<EvaluationBeanNew.WordsBean> wordsBeanList){
        spannable = new SpannableStringBuilder();

        for (EvaluationBeanNew.WordsBean wordsBean:wordsBeanList){
            setWord(wordsBean);
        }

        return spannable;
    }

    //设置单词样式
    //这里用的是小于，原来是小于等于2.5，所以这里设置成2.6
    public static final double errorWordScore = 2.6;
    //这里用的是大于，原来是大于等于4，所以这里可以设置成4.1，但是不让显示正确单词，所以提高数据
    public static final double rightWordScore = 10;

    public static void setWord(EvaluationBeanNew.WordsBean word){
        String content = word.getContent();

        if (TextUtils.isEmpty(content)){
            spannable.append("");
        }else if (Double.parseDouble(word.getScore())<errorWordScore){
            spannable.append(setSpan(content,Color.RED));
        }else if (Double.parseDouble(word.getScore())>rightWordScore){
            spannable.append(setSpan(content,Color.GREEN));
        }else {
            spannable.append(setSpan(content,Color.BLACK));
        }
    }

    //设置标记单词的颜色
    public static SpannableStringBuilder setSpan(String wordSpan,int spanColor){
        SpannableStringBuilder builder = new SpannableStringBuilder(wordSpan.trim());
        String s_str = String.valueOf(wordSpan.charAt(0));
        String e_str = String.valueOf(wordSpan.charAt(wordSpan.length()-1));

        String rule = "\\p{P}";

        int s_int = 0;
        int e_int = wordSpan.length()-1;

        if (s_str.matches(rule)){
            s_int++;
        }

        if (e_str.matches(rule)){
            e_int--;
        }

        if (s_int>e_int){
            builder.setSpan(new ForegroundColorSpan(Color.BLACK),0,wordSpan.length(),SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }else {
            if (s_int>0){
                builder.setSpan(new ForegroundColorSpan(Color.BLACK),0,s_int,SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            if (e_int < wordSpan.length()-1){
                builder.setSpan(new ForegroundColorSpan(Color.BLACK),e_int,wordSpan.length(),SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            builder.setSpan(new ForegroundColorSpan(spanColor),s_int,e_int+1,SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }
}

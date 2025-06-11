package com.iyuba.core.lil.model.remote.manager;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.RemoteHelper;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect_show;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Mix_result;
import com.iyuba.core.lil.model.remote.bean.Publish_result;
import com.iyuba.core.lil.model.remote.bean.Word_collect;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.model.remote.service.LibCollectService;
import com.iyuba.core.lil.model.remote.service.LibEvalService;
import com.iyuba.core.lil.model.remote.service.LibWordService;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_result;
import com.iyuba.core.lil.remote.bean.Alipay_info;
import com.iyuba.core.lil.remote.bean.Login_account;
import com.iyuba.core.lil.remote.bean.Mob_verify;
import com.iyuba.core.lil.remote.bean.Reward_history;
import com.iyuba.core.lil.remote.bean.User_info;
import com.iyuba.core.lil.remote.bean.Word_note;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;
import com.iyuba.core.lil.remote.service.LibAdService;
import com.iyuba.core.lil.remote.service.LibPayService;
import com.iyuba.core.lil.remote.service.LibUserInfoService;
import com.iyuba.core.lil.remote.service.LibWalletService;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.lil.util.LibEncodeUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 部分接口操作
 */
public class LessonRemoteManager {

    /**************************************用户信息*****************************/
    //获取用户信息-20001
    public static Observable<User_info> getUserInfo(long userId){
        String url = "http://api."+Constant.IYBHttpHead2()+"/v2/api.iyuba";

        int protocol = 20001;
        int appId = Integer.parseInt(Constant.APPID);
        String format = "json";
        String platform = "android";

        String sign = LibEncodeUtil.md5(protocol+""+userId+"iyubaV2");

        LibUserInfoService infoService = RemoteHelper.getInstance().createJson(LibUserInfoService.class);
        return infoService.getUserInfo(url,protocol,appId,userId,userId,format,sign,platform);
    }

    //账号登录-11001
    public static Observable<Login_account> loginByAccount(String userName, String password){
        //http://api.iyuba.com.cn/v2/api.iyuba
        String url = "http://api."+Constant.IYBHttpHead2()+"/v2/api.iyuba";

        int protocol = 11001;
        String longitude = "";
        String latitude = "";
        int appId = Integer.parseInt(Constant.APPID);
        String format = "json";
        String sign = LibEncodeUtil.md5(protocol+userName+ LibEncodeUtil.md5(password)+"iyubaV2");

        userName = LibEncodeUtil.encode(userName);
        password = LibEncodeUtil.md5(password);

        LibUserInfoService userService = RemoteHelper.getInstance().createJson(LibUserInfoService.class);
        return userService.loginByAccount(url,protocol,appId,longitude,latitude,format,userName,password,sign);
    }

    //秒验查询用户信息-10010
    public static Observable<Mob_verify> mobVerifyFromServer(String token,String opToken,String operator){
        String url = "http://api."+Constant.IYBHttpHead2()+"/v2/api.iyuba";

        int protocol = 10010;
        int appId = Integer.parseInt(Constant.APPID);
        String mobKey = Constant.SMSAPPID2;
        token = LibEncodeUtil.encode(token);

        LibUserInfoService userService = RemoteHelper.getInstance().createJson(LibUserInfoService.class);
        return userService.loginByMob(url,protocol,token,opToken,operator,appId,mobKey);
    }

    /***************************************钱包信息********************************/
    //获取用户钱包历史记录
    public static Observable<BaseBean_data<List<Reward_history>>> getWalletHistory(int userId, int pageIndex, int showCount){
        String url = "http://api."+Constant.IYBHttpHead()+"/credits/getuseractionrecord.jsp";
        String sign = userId+"iyuba"+ LibDateUtil.toDateStr(System.currentTimeMillis(),LibDateUtil.YMD);
        sign = LibEncodeUtil.md5(sign);

        LibWalletService service = RemoteHelper.getInstance().createJson(LibWalletService.class);
        return service.getWalletHistory(url,userId,pageIndex,showCount,sign);
    }

    /*****************************************广告信息*******************************/
    //获取通用广告信息
    public static Observable<List<Ad_result>> getAdInfo(int adFlag, int userId){
        String url = "http://dev."+Constant.IYBHttpHead()+"/getAdEntryAll.jsp";

        int appId = Integer.parseInt(Constant.APPID);

        LibAdService adService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return adService.getAdInfo(url,userId,appId,adFlag);
    }

    /********************************************支付接口******************************/
    //支付宝支付
    public static Observable<Alipay_info> getAlipay(int uid, String amount, String productId, String subject, String free, String body){
        String url = "http://vip."+Constant.IYBHttpHead()+"/alipay.jsp";

        String appId = Constant.APPID;
        String sign = LibEncodeUtil.md5(uid+"iyuba"+LibDateUtil.toDateStr(System.currentTimeMillis(),LibDateUtil.YMD));

//        try {
//            body = LibEncodeUtil.encode(body);
//            subject = LibEncodeUtil.encode(subject);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        LibPayService payService = RemoteHelper.getInstance().createJson(LibPayService.class);
        return payService.getAlipay(url,amount,sign,productId,body,subject,appId,uid,free);
    }

    /*********************************************单词信息*****************************/
    //获取生词本数据
    public static Observable<Word_note> getWordNoteData(int userId,int pageNum,int shoCount){
        String url = "http://word."+Constant.IYBHttpHead()+"/words/wordListService.jsp";

        LibWordService wordService = RemoteHelper.getInstance().createXml(LibWordService.class);
        return wordService.getWordNoteData(url,userId,pageNum,shoCount);
    }

    //收藏/取消收藏单词
    public static Observable<Word_collect> collectWordData(int userId, String mode, String word){
        String url = "http://word."+Constant.IYBHttpHead()+"/words/updateWord.jsp";

        String groupName = "Iyuba";

        LibWordService wordService = RemoteHelper.getInstance().createXml(LibWordService.class);
        return wordService.collectWord(url,userId,mode,groupName,word);
    }

    //查询单词相关内容
    public static Observable<Word_search> searchWordData(String word,int userId,int pageIndex,int showCount){
        String url = "http://apps."+Constant.IYBHttpHead()+"/iyuba/searchApiNew.jsp";

        String type = "voa";
        int parentId = 0;
        String format = "json";
        int flg = 0;

        LibWordService wordService = RemoteHelper.getInstance().createJson(LibWordService.class);
        return wordService.searchWord(url,format,word,pageIndex,showCount,parentId,type,flg,userId);
    }

    /***********************************************文章收藏*****************************/
    //获取收藏的文章数据
    public static Observable<Chapter_collect_show> getCollectVoaData(int userId){
        String url = "http://daxue."+Constant.IYBHttpHead()+"/appApi/getCollect.jsp";

        String appName = "familyalbum";
        String groupName = "Iyuba";
        String type = "voa";
        int flag = 0;

        LibCollectService collectService = RemoteHelper.getInstance().createXml(LibCollectService.class);
        return collectService.getChapterCollectData(url,userId,groupName,type,flag,appName);
    }

    //收藏/取消收藏文章
    public static Observable<Chapter_collect> collectVoaData(int userId,int voaId,boolean isDel){
        String url = "http://daxue."+Constant.IYBHttpHead()+"/appApi/updateCollect.jsp";

        String groupName = "Iyuba";
        int flag = 0;
        String topic = "familyalbum";
        int sentenceId = 0;

        String type = "insert";
        if (isDel){
            type = "del";
        }

        LibCollectService collectService = RemoteHelper.getInstance().createXml(LibCollectService.class);
        return collectService.collectVoa(url,userId,voaId,sentenceId,type,groupName,flag,topic);
    }

    /*************************************************文章评测****************************/
    //提交文章句子评测
    public static Observable<BaseBean_data<Eval_result>> submitEvalSentenceData(int voaId,int userId,String paraId,String lineN,String sentence,String filePath){
        String url = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/test/ai/";

        String flag = "0";
        String newsId = String.valueOf(voaId);
        String idIndex = lineN;
        String appId = Constant.APPID;
        String wordId = "0";
        String type = "familyalbum";

        sentence = LibEncodeUtil.encode(sentence).replaceAll("\\+","%20");

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sentence",sentence)
                .addFormDataPart("flg",flag)
                .addFormDataPart("paraId",paraId)
                .addFormDataPart("newsId",newsId)
                .addFormDataPart("IdIndex",idIndex)
                .addFormDataPart("appId",appId)
                .addFormDataPart("wordId",wordId)
                .addFormDataPart("type",type)
                .addFormDataPart("userId",String.valueOf(userId))
                .addFormDataPart("file",file.getPath(),fileBody)
                .build();

        LibEvalService evalService = RemoteHelper.getInstance().createJson(LibEvalService.class);
        return evalService.submitEvalData(url,multipartBody);
    }

    //发布单句评测到排行榜
    public static Observable<Publish_result> publishEvalSentence(int voaId,int userId,String userName,String lineN,String audioUrl,String score){
        String url = "http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi";

        String topic = "familyalbum";
        String platform = "android";
        String format = "json";
        int protocol = 60003;
        int shuoshuotype = 2;

        String idIndex = lineN;

        LibEvalService evalService = RemoteHelper.getInstance().createJson(LibEvalService.class);
        return evalService.publishSentence(url,topic,platform,format,protocol,userId,userName,voaId,idIndex,score,shuoshuotype,audioUrl);
    }

    //合成评测句子
    public static Observable<Mix_result> mixEvalSentence(int voaId,int userId){
        String url = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/test/merge/";

        String type = "familyalbum";

        //这里根据上边传来的数据进行排序合成
        StringBuffer buffer = new StringBuffer();
        List<EvalSentenceEntity> evalList = HelpDataManager.getInstance().getMultiEvalData(voaId, userId);
        for (int i = 0; i < evalList.size(); i++) {
            EvalSentenceEntity tempEval = evalList.get(i);
            buffer.append(tempEval.audioUrl);
            if (i != evalList.size()-1){
                buffer.append(",");
            }
        }

        String mixSentenceUrl = buffer.toString();

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audios",mixSentenceUrl)
                .addFormDataPart("type",type)
                .build();

        LibEvalService evalService = RemoteHelper.getInstance().createJson(LibEvalService.class);
        return evalService.margeEvalSentence(url,body);
    }

    //发布合成评测到排行榜
    public static Observable<Publish_result> publishMixEvalSentence(int voaId,int userId,String userName,String score,String audioUrl){
        String url = "http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi";

        String topic = "familyalbum";
        String platform = "android";
        String format = "json";
        int protocol = 60003;
        int shuoshuotype = 4;
        int rewardVersion = 1;

        /*MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("topic",topic)
                .addFormDataPart("platform",platform)
                .addFormDataPart("format",format)
                .addFormDataPart("protocol",String.valueOf(protocol))
                .addFormDataPart("userid",String.valueOf(userId))
                .addFormDataPart("username",userName)
                .addFormDataPart("voaid",String.valueOf(voaId))
                .addFormDataPart("score",score)
                .addFormDataPart("shuoshuotype",String.valueOf(shuoshuotype))
                .addFormDataPart("content",audioUrl)
                .addFormDataPart("rewardVersion",String.valueOf(rewardVersion))
                .build();*/

        LibEvalService evalService = RemoteHelper.getInstance().createJson(LibEvalService.class);
        return evalService.publishMixSentence(url,topic,platform,format,protocol,userId,userName,voaId,score,shuoshuotype,audioUrl,rewardVersion);
    }
}

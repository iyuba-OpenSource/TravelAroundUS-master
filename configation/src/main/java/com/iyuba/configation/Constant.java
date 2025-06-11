package com.iyuba.configation;

import android.os.Environment;

import java.io.File;

public class Constant {
    public static String APPName = "走遍美国";// 应用名称 Vivo走遍美国学英语
    public static String APPType = "FamilyAlbum";// 爱语吧承认的英文缩写
    public static String APPID = "230";// 走遍美国确认为230 爱语吧id
    public static String TOPICID = "familyalbum";//应用标记类型

    //key值数据-引导界面标志
    public static final int GUIDE_VERSION = 65;

    //	public static String envir = ConfigManager.Instance().loadString("envir",
//			RuntimeManager.getContext().getExternalFilesDir(null).toString() + "/");// 文件夹路径
    public final static String envir = Environment
            .getExternalStorageDirectory() + "/Android/data/" + "com.iyuba.American" + "/iyuba/travel/";//文件夹路径
    public static final String UM_KEY = "541fdfb0fd98c52f71002dad";
    //public static String APPID = "224";
    public static String appfile = "familyalbum";// 更新时的前缀名 newconcept
    public static String append = ".mp3";// 文件append
    public static String videoAddr = envir + "/audio/";// 音频文件存储位置
    public static String picSrcAddr = envir + "/pic/";// 音频文件存储位置
    private static String simRecordAddr = envir + "/audio/sound";
    private static String recordTag = ".amr";// 录音（跟读所用）的位置

    //请求头
//    public static String IYBHttpHead = "iyuba.cn";//爱语吧统一请求地址头部，更新于2019.1.10 原为 iyuba.com  '+Constant.IYBHttpHead+'
//    public static String IYBHttpHead2 = "iyuba.com.cn";
    public static String IYBHttpHead(){
        return ConfigManager.Instance().getDomainShort();
    }
    public static String IYBHttpHead2(){
        return ConfigManager.Instance().getDomainLong();
    }

    //	public static String picAddr = RuntimeManager.getContext()
//			.getExternalCacheDir().getAbsolutePath();// imagedownloader默认缓存图片位置
    public static String AppIcon(){
        return "http://app." + Constant.IYBHttpHead() + "/android/images/FamilyAlbum/FamilyAlbum.png";
    }

    public static String picAddr = Environment
            .getExternalStorageDirectory() + "/Android/data/" + "com.iyuba.American" + "/iyuba/travel/";
    public static String userAddr = envir + "/user.jpg";// 用户头像，已废弃
    public static String recordAddr = envir + "/sound.amr";// 跟读音频
    public static String voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
    public static String screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    public static String mWeiXinKey = "wxfa4799656aff72d9";

    //应用宝微链接
    //public static String shareUrl = "https://a.app.qq.com/o/simple.jsp?pkgname=com.iyuba.American";
    //自己的链接
    public static String shareUrl = "http://app." + Constant.IYBHttpHead() + "/androidApp.jsp?id=230&appId=57";
    /**
     * 上传能力测评使用的url
     */
    public static String url_updateExamRecord(){
        return "http://daxue." + Constant.IYBHttpHead() + "/ecollege/updateExamRecord.jsp";
    }

    //以下为智能能力测试所用常量
    public static String[] ABILITY_TYPE_ARR = {"写作", "单词", "语法", "听力", "口语", "阅读"};
    /**
     * 写作能力测试代码
     */
    public static final int ABILITY_TETYPE_WRITE = 0;
    public static final String ABILITY_WRITE = "X";
    public static final String[] WRITE_ABILITY_ARR = {"写作表达", "写作结构", "写作逻辑", "写作素材"};

    /**
     * 单词测试代码
     */
    public static final int ABILITY_TETYPE_WORD = 1;
    public static final String ABILITY_WORD = "W";
    public static final String[] WORD_ABILITY_ARR = {"中英力", "英中力", "发音力", "音义力", "拼写力", "应用力"};


    /**
     * 语法能力测试代码
     */
    public static final int ABILITY_TETYPE_GRAMMER = 2;
    public static final String ABILITY_GRAMMER = "G";
    public static final String[] GRAM_ABILITY_ARR = {"名词", "代词", "形容词副词", "动词", "时态", "句子"};

    /**
     * 听力能力测试代码
     */
    public static final int ABILITY_TETYPE_LISTEN = 3;
    public static final String ABILITY_LISTEN = "L";
    public static final String[] LIS_ABILITY_ARR = {"准确辨音", "听能逻辑", "音义匹配", "听写"};

    /**
     * 口语能力测试代码
     */
    public static final int ABILITY_TETYPE_SPEAK = 4;
    public static final String ABILITY_SPEAK = "S";
    public static final String[] SPEAK_ABILITY_ARR = {"发音", "表达", "素材", "逻辑"};

    /**
     * 阅读能力测试代码
     */
    public static final int ABILITY_TETYPE_READ = 5;
    public static final String ABILITY_READ = "R";
    public static final String[] READ_ABILITY_ARR = {"词汇认知", "句法理解", "语义和逻辑", "语篇"};


    /**
     * 单选能力测试
     */
    public static final int ABILITY_TESTTYPE_SINGLE = 1;
    /**
     * 填空题
     */
    public static final int ABILITY_TESTTYPE_BLANK = 2;
    /**
     * 选择填空
     */
    public static final int ABILITY_TESTTYPE_BLANK_CHOSE = 3;
    /**
     * 图片选择
     */
    public static final int ABILITY_TESTTYPE_CHOSE_PIC = 4;

    /**
     * 语音评测
     */
    public static final int ABILITY_TESTTYPE_VOICE = 5;
    /**
     * 多选
     */
    public static final int ABILITY_TESTTYPE_MULTY = 6;
    /**
     * 判断题目
     */
    public static final int ABILITY_TESTTYPE_JUDGE = 7;
    /**
     * 单词拼写
     */
    public static final int ABILITY_TESTTYPE_BLANK_WORD = 8;

    /**
     * 新概念能力测试 听力url前缀  http://static2.'+Constant.IYBHttpHead+'/IELTS/sounds/16819.mp3
     */
    public static String ABILITY_AUDIO_URL_PRE(){
        return "http://static2." + Constant.IYBHttpHead() + "/NewConcept1/sounds/";
    }
    /**
     * 新概念能力测试 附件url前缀 http://static2.'+Constant.IYBHttpHead+'/IELTS/attach/9081.txt
     */
    public static String ABILITY_ATTACH_URL_PRE(){
        return "http://static2." + Constant.IYBHttpHead() + "/NewConcept1/attach/";
    }
    /**
     * 新概念能力测试 图片url前缀  http://static2."+Constant.IYBHttpHead+"/IELTS/images/
     */
    public static String ABILITY_IMAGE_URL_PRE(){
        return "http://static2." + Constant.IYBHttpHead() + "/NewConcept1/images/";
    }

    //发短信
    public final static String SMSAPPID = "1082478e5d73b";
    public final static String SMSAPPSECRET = "bdf2a87632e39370c6bb5159b4d6a8d6";

    public final static String SMSAPPID2 = "19a1773990a19";//da2d6fdc5cb2
    public final static String SMSAPPSECRET2 = "e05c1fa7afaff36759e5728b6157f78b";//6bd3183af2d993d296d23a28ef7aeb13

    public static int recordId;// 学习记录篇目id，用于主程序
    public static String recordStart;// 学习记录开始时间，用于主程序

    public static int normalColor = 0xff414141;//0xff414141
    public static int whiteColor = 0xffffffff;//
    public final static int readColor = 0xff3663F8;//#3663F8
    public final static int unreadCnColor = 0xff333333;
    public final static int selectColor = 0xffde5e5b;
    public final static int unselectColor = 0xff444444;
    public final static int optionItemSelect = 0x7fbdfaf1;
    public final static int optionItemUnselect = 0xff8ab6da;

    public static int textColor = 0xff2983c1;
    public static int textSize = 18;

    public static int mode;// 播放模式
    public static int type;// 听歌播放模式
    public static int download;// 是否下载
    //http://static3."+Constant.IYBHttpHead+"/...
    //http://api."+Constant.IYBHttpHead+"/mobile/android/familyalbum/islatest.plain?currver=5
    //public static String appUpdateUrl = "http://api."+Constant.IYBHttpHead+"/mobile/android/newconcept/islatest.plain?currver=";// 升级地址
    public static String appUpdateUrl(){
        return "http://api." + Constant.IYBHttpHead() + "/mobile/android/familyalbum/islatest.plain?currver=";// 升级地址
    }
    public static String feedBackUrl(){
        return "http://api." + Constant.IYBHttpHead() + "/mobile/android/familyalbum/feedback.plain?uid=";// 反馈
    }
    public static String sound(){
        return "http://static2." + Constant.IYBHttpHead() + "/FamilyAlbum/";//音频头http://static2."+Constant.IYBHttpHead+"/newconcept/
    }
    public static String sound_vip(){
        return "http://staticvip2." + Constant.IYBHttpHead() + "/FamilyAlbum/";//Vip音频头http://staticvip2."+Constant.IYBHttpHead+"/newconcept/
    }
    public static String wordUrl(){
        return "http://word." + Constant.IYBHttpHead() + "/words/apiWord.jsp?q=";
    }

    public static String addCreditsUrl(){
        return "http://api." + Constant.IYBHttpHead() + "/credits/updateScore.jsp?";
    }


    //爱语微课所用

    //移动课堂所需的相关API
    public static String MOB_CLASS_DOWNLOAD_PATH(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/";
    }
    public static String MOB_CLASS_PAYEDRECORD_PATH(){
        return "http://app." + Constant.IYBHttpHead() + "/pay/apiGetPayRecord.jsp?";
    }
    public static String MOB_CLASS_PACK_IMAGE(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/packIcon/";
    }
    public static String MOB_CLASS_PACK_TYPE_IMAGE(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/nmicon/";
    }

    public static String MOB_CLASS_COURSE_IMAGE(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/";
    }

    public final static String reqPackDesc = "class.jichu";
    public final static int IO_BUFFER_SIZE = 100 * 1024;

    public static String PIC_BASE_URL(){
        return "http://app." + Constant.IYBHttpHead() + "/dev/";
    }

    public static String MOB_CLASS_COURSE_RESOURCE_DIR(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/package";
    }
    public final static String MOB_CLASS_COURSE_RESOURCE_APPEND = ".zip";

    public static String MOB_CLASS_PACK_BGPIC(){
        return "http://static3." + Constant.IYBHttpHead() + "/resource/categoryIcon/";
    }

    public final static String JLPT1_APPID = "205";//日语一级id
    public final static String JLPT2_APPID = "206";//日语二级id
    public final static String JLPT3_APPID = "203";//日语三级id
    public final static String CET4_APPID = "207";//日语三级id
    public final static String CET6_APPID = "208";//日语三级id

    //日志音频地址 ，非VIP
    public static String AUDIO_ADD(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/sounds";
    }
    //日志音频地址 ，VIP
    public static String AUDIO_VIP_ADD(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/sounds";
    }

    //日志视频地址 ，VIP
    public static String VIDEO_VIP_ADD(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/video";
    }
    //日志视频地址 ，非VIP
    public static String VIDEO_ADD(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/video";
    }
    public static String IMAGE_DOWN_PATH(){
        return "http://api." + Constant.IYBHttpHead2() + "/v2/api.iyuba?protocol=10005&size=big&uid=";
    }
    public static String PIC_ABLUM__ADD(){
        return "http://static1." + Constant.IYBHttpHead() + "/data/attachment/album/";
    }

    public final static String MicroClassReqPackId = "21";

    public static final int testtype = 4;
    public static String urlPerfix(){
        return "http://cetsounds." + Constant.IYBHttpHead() + "/" + testtype + "/";
    }
    public static String vipurlPerfix(){
        return "http://cetsoundsvip." + Constant.IYBHttpHead() + "/" + testtype + "/";
    }

    public static String userimage(){
        return "http://api." + Constant.IYBHttpHead2() + "/v2/api.iyuba?protocol=10005&uid=";//用户头像获取地址
    }

    // 听歌中用

    public static String detailUrl(){
        return "http://apps." + Constant.IYBHttpHead() + "/afterclass/getText.jsp?SongId=";//原文地址
    }
    public static String lrcUrl(){
        return "http://apps." + Constant.IYBHttpHead() + "/afterclass/getLyrics.jsp?SongId=";//原文地址，听歌专用
    }

    public static String searchUrl(){
        return "http://apps." + Constant.IYBHttpHead() + "/afterclass/searchApi.jsp?key=";//查询
    }
    public static String titleUrl(){
        return "http://apps." + Constant.IYBHttpHead() + "/afterclass/getSongList.jsp?maxId=";//新闻列表，主程序用
    }
    public static String vipurl(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/sounds/song/";//vip地址
    }
    public static String songurl(){
        return "http://staticvip." + Constant.IYBHttpHead() + "/sounds/song/";//普通地址
    }
    public static String soundurl(){
        return "http://static2." + Constant.IYBHttpHead() + "/go/musichigh/";//1000之前解析地址
    }

    //以上为爱语微课相关

    public static void reLoadData() {
//		envir = ConfigManager.Instance().loadString("envir");// 文件夹路径
        videoAddr = envir + "/audio/";// 音频文件存储位置
        recordAddr = envir + "/sound.amr";// 跟读音频
        voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
        screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    }


    public static String getsimRecordAddr() {
        File file = new File(simRecordAddr);
        if (!file.exists()){
            file.mkdirs();
        }
        return simRecordAddr;
    }

    public static String getrecordTag() {
        return recordTag;
    }

    /**************************广告***************************/
    //有道广告
    public static final String YOUDAO_SPLASH_CODE = "a710131df1638d888ff85698f0203b46";
    public static final String YOUDAO_STREAM_CODE = "";
    public static final String YOUDAO_BANNER_CODE = "230d59b7c0a808d01b7041c2d127da95";

    //爱语吧广告
    //com.iyuba.American 开屏 0073
    //com.iyuba.American banner 0074
    public static final String SDK_SPLASH_CODE = "0073";
    public static final String SDK_BANNER_CODE = "0074";

    /****************************微课审核**********************/
    //是否需要控制显示
    public static boolean mocVerifyCheck = true;

    //获取微课的控制渠道id
    public static int getMocLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 9001;
            case "xiaomi":
                return 9002;
            case "oppo":
                return 9003;
            case "vivo":
                return 9004;
        }
        return 9000;
    }

    /****************************视频审核**********************/
    //是否需要控制显示
    public static boolean videoVerifyCheck = true;

    //获取视频的控制渠道id
    public static int getVideoLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 9021;
            case "xiaomi":
                return 9022;
            case "oppo":
                return 9023;
            case "vivo":
                return 9024;
        }
        return 9020;
    }

    /**********************************其他内容****************************/
    //会员支付说明链接
    public static String VIP_AGREEMENT_URL = "http://iuserspeech."+ Constant.IYBHttpHead()+ ":9001/api/vipServiceProtocol.jsp?company=1&type=app";
}
package com.iyuba.core.common.retrofitapi;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.result.WordEvalResult;
import com.iyuba.core.common.retrofitapi.result.WordExplainResult;
import com.iyuba.core.lil.bean.AppCheckResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface OtherApi {

    String BASE_URL = "http://api."+ Constant.IYBHttpHead2()+"/";

    //审核接口
    String APP_VERIFY_URL = "http://api.qomolama.cn/getRegisterAll.jsp";
    //单词释义接口
    String WORD_EXPLAIN_URL = "http://word."+Constant.IYBHttpHead()+"/words/apiWordAi.jsp";
    //单词评测接口
    String WORD_EVAL_URL = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/test/ai10/";

    //获取单词的释义
    @GET
    Observable<WordExplainResult> getWordExplain(@Url String url,@Query("q") String word, @Query("user_pron") String user_pron, @Query("ori_pron") String ori_pron,
                                      @Query("appid") int appid, @Query("uid") int uid);
    //提交单词评测数据
    @Multipart
    @POST
    Observable<WordEvalResult> uploadWordEval(@Url String url,@PartMap Map<String, RequestBody> optionMap,
                                              @Part MultipartBody.Part file);

    //审核接口-可用于控制微课显示、视频显示、人教版显示
    @GET
    Observable<AppCheckResponse> appVerify(@Url String url, @Query("appId") int appId, @Query("appVersion") String version);

    interface WordEval{
        interface Param{
            interface Key{
                String TYPE = "type";
                String SENTENCE = "sentence";
                String USERID = "userId";
                String NEWSID = "newsId";
                String PARAID = "paraId";
                String IDINDEX = "IdIndex";
            }

            interface Value{

            }
        }
    }
}

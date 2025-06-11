package com.iyuba.core.lil.model.local.manager;

import com.iyuba.core.lil.model.local.LibRoomDB;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;

import java.util.List;

/**
 * @title: 辅助数据管理
 * @date: 2023/12/20 10:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class HelpDataManager {
    private static HelpDataManager instance;

    public static HelpDataManager getInstance(){
        if (instance==null){
            synchronized (HelpDataManager.class){
                if (instance==null){
                    instance = new HelpDataManager();
                }
            }
        }
        return instance;
    }

    /*************************课程收藏**********************/
    //获取当前账号下的课程收藏数据
    public List<VoaCollectEntity> getVoaCollectByUserId(int userId){
        return LibRoomDB.getInstance().getVoaCollectDao().getMultiData(userId);
    }

    //获取当前课程收藏数据
    public VoaCollectEntity getSingleVoaCollectData(int userId,int voaId){
        return LibRoomDB.getInstance().getVoaCollectDao().getSingleData(voaId,userId);
    }

    //删除单个课程收藏数据
    public void deleteSingleVoaCollectData(int userId,int voaId){
        LibRoomDB.getInstance().getVoaCollectDao().deleteSingleData(voaId,userId);
    }

    //删除多个课程收藏数据
    public void deleteMultiVoaCollectData(int userId,List<Integer> voaIdList){
        if (voaIdList!=null&&voaIdList.size()>0){
            for (int i = 0; i < voaIdList.size(); i++) {
                LibRoomDB.getInstance().getVoaCollectDao().deleteSingleData(voaIdList.get(i),userId);
            }
        }
    }

    //插入单个课程收藏数据
    public void saveSingleVoaCollectData(VoaCollectEntity collect){
        LibRoomDB.getInstance().getVoaCollectDao().saveSingleData(collect);
    }

    /*************************课程试听**********************/
    //获取当前账号下的课程试听数据
    public List<VoaListenEntity> getVoaListenByUserId(int userId){
        return LibRoomDB.getInstance().getVoaListenDao().getMultiData(userId);
    }

    //获取当前课程试听数据
    public VoaListenEntity getSingleVoaListenData(int userId,int voaId){
        return LibRoomDB.getInstance().getVoaListenDao().getSingleData(voaId,userId);
    }

    //删除单个课程试听数据
    public void deleteSingleVoaListenData(int userId,int voaId){
        LibRoomDB.getInstance().getVoaListenDao().deleteSingleData(voaId,userId);
    }

    //删除多个课程试听数据
    public void deleteMultiVoaListenData(int userId,List<Integer> voaIdList){
        if (voaIdList!=null&&voaIdList.size()>0){
            for (int i = 0; i < voaIdList.size(); i++) {
                LibRoomDB.getInstance().getVoaListenDao().deleteSingleData(voaIdList.get(i),userId);
            }
        }
    }

    //插入单个课程试听数据
    public void saveSingleVoaListenData(VoaListenEntity collect){
        LibRoomDB.getInstance().getVoaListenDao().saveSingleData(collect);
    }

    /*************************************句子评测*********************************/
    //保存单个句子评测数据
    public void saveSingleEvalData(EvalSentenceEntity evalSentenceEntity){
        LibRoomDB.getInstance().getEvalSentenceDao().saveSingleData(evalSentenceEntity);
    }

    //获取单个句子评测数据
    public EvalSentenceEntity getSingleEvalData(int voaId,int userId,String paraId,String lineN){
        return LibRoomDB.getInstance().getEvalSentenceDao().getSingleEvalData(voaId, userId, paraId, lineN);
    }


    //获取多个句子评测数据
    public List<EvalSentenceEntity> getMultiEvalData(int voaId,int userId){
        return LibRoomDB.getInstance().getEvalSentenceDao().getMultiEvalData(voaId, userId);
    }
}

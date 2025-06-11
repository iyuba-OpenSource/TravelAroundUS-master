package com.iyuba.concept2.sqlite.mode;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.user.UserInfoManager;

/**
 * Comment generated after reading in the voice evaluating part
 */
public class ReadVoiceComment extends Comment implements Shareable {
	private static final String TAG = ReadVoiceComment.class.getSimpleName();

	private Voa voaRef;
	private VoaDetail detailRef;
	private boolean shareArtical = false;
	public ReadVoiceComment() {
		super();
		shuoshuoType = 2;
	}

	public ReadVoiceComment(Voa voa) {
		this();
		this.voaRef = voa;		
		this.detailRef = null;
	}
	public ReadVoiceComment(Voa voa,boolean shareArtical) {
		this();
		this.voaRef = voa;
		this.shareArtical = shareArtical;
		this.detailRef = null;
	}
	public ReadVoiceComment(Voa voa, VoaDetail detail) {
		this();
		this.voaRef = voa;
		this.detailRef = detail;
	}

	public Voa getVoaRef() {
		return this.voaRef;
	}

	public VoaDetail getVoaDetailRef() {
		return this.detailRef;
	}

	@Override
	public String getShareUrl() {
		//return "http://daxue."+Constant.IYBHttpHead+"/appApi/play.jsp?id=" + id + "&appid=" + Constant.APPID;
		return "http://voa."+Constant.IYBHttpHead()+"/voa/play.jsp?id="+id;
	}

	public String getArticleShareUrl() {
		return Constant.shareUrl;
	}
	
	@Override
	public String getShareImageUrl() {
//		return voaRef.pic;
		return "http://app."+Constant.IYBHttpHead()+"/android/images/FamilyAlbum/FamilyAlbum.png";
        //http://app."+Constant.IYBHttpHead+"/android/images/newconcept/newconcept.png

	}
	
	@Override
	public String getShareAudioUrl() {
		return "http://daxue."+Constant.IYBHttpHead()+"/appApi/" + shuoshuo;
	}

	@Override
	public String getShareTitle() {
		if(shareArtical){
			if(voaRef!=null)
			return "[我正在走遍美国中读:"
                + voaRef.title+" "+voaRef.titleCn+ "这篇课文,非常有意思，大家快来读吧！] ";
			return "[我正在走遍美国中读一篇非常有意思的课文，大家快来使用吧！]";
		}else {
			String userName = (UserInfoManager.getInstance().isLogin()) ? "["
					+ UserInfoManager.getInstance().getUserName() + "]" : "";
			//return userName + "在" + "爱语吧语音评测中获得了" +  + "分";
			if(detailRef!=null)
				return userName + "在" + "走遍美国语音评测中获得了"+detailRef.getReadScore()+"分";
			else {
				return userName + "在" + "走遍美国语音评测中获得了100分";
			}
		}

	}

	@Override
	public String getShareLongText() {
//		return detailRef.sentence + " " + detailRef.sentenceCn;
		return voaRef.title+" "+voaRef.titleCn;
	}

	@Override
	public String getShareShortText() {
//		return detailRef.sentence;
		return voaRef.title;
	}

	public String getArticleShareShortText() {
		return voaRef.title;
	}
	
	public String getArticleShareShortTextCn() {
		return voaRef.titleCn;
	}
	
	@Override
	public String toString() {
		return TAG + "[id=" + id + "shuoshuo=" + shuoshuo + "voaRef.id="
				+ voaRef.voaId + "detailRef.sentence=" + detailRef.sentence + "]";
	}

}

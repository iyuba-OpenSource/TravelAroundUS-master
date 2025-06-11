package com.iyuba.concept2.manager;
//package com.iyuba.concept2.activity.manager;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Random;
//
//import com.iyuba.concept2.activity.sqlite.mode.LessonExercise;
//import com.iyuba.concept2.activity.sqlite.mode.MultipleChoice;
//import com.iyuba.concept2.frame.runtimedata.RuntimeManager;
//
//import android.content.Context;
//
///**
// * 做题管理
// */
//public class ExerciseManager {
//	private Context mContext;
//	private boolean hasFormer = false;//有上一题
//	private boolean hasNext = true;//有下一题
//	private int currQuestionId = 0;  //指示当前题号
//	private List<MultipleChoice> currLessonExercise= new ArrayList<MultipleChoice>();		//当前所听文章的试题列表
//	public boolean isTesting = false;//是否正在做测试
//	public int testStatus ;//测试状态，0：只开始听；1：听力完成；2：做题完成', 
//	public String currLessonId="";//在做哪篇的听力
//	
//	public ExerciseManager(String voaid) {
//		super();
//		currLessonId = voaid;
//		mContext = RuntimeManager.getContext();
//	}
//	public ExerciseManager(String lessonId,int questionId) {
//		super();
//		
//		currLessonId = lessonId;
//		currQuestionId = questionId;
//		getCurrQuestionId();
//		mContext = RuntimeManager.getContext();
//	}
//
//	/**
//	 * @param mList  赋值的列表,每次赋值会随机选择题目
//	 * 
//	 * 从列表中随机选择2填空1选择
//	 */
//	public void setCurrLessonExercises(List<MultipleChoice> mList){
//		int a;    		//第一个选择题位置
//		int b;			//第二个选择题位置
//		int i;
//			Random random =new Random();
//			//随机添加第一个选择题
//			for(;;){
//				i = random.nextInt(mList.size());
//				if (mList.get(i).excType.equals("1")) 
//				{
//					a=i;
//					currLessonExercise.add(mList.get(i));
//				break;
//				}
//			}
//			//随机添加第二个选择题
//			for(;;){
//				i = random.nextInt(mList.size());
//					if (mList.get(i).excType.equals("1")&&a!=i) 
//					{
//						b=i;
//						currLessonExercise.add(mList.get(i));
//					break;
//					}
//			}
//			
//			//添加填空题
//			for (Iterator iterator = mList.iterator(); iterator.hasNext();) {
//				LessonExercise lessonExercise = (LessonExercise) iterator.next();
//				if (lessonExercise.excType.equals("2")) {
//					currLessonExercise.add(lessonExercise);
//					break;
//				}
//			}
//			
//			if (currLessonExercise.size()==2) {
//				for (Iterator iterator = mList.iterator(); iterator.hasNext();) {
//					LessonExercise lessonExercise = (LessonExercise) iterator.next();
//					if (lessonExercise.excType.equals("3")) {
//						currLessonExercise.add(lessonExercise);
//						break;
//					}
//				}
//			}
//			if (currLessonExercise.size()==2) {
//				for( ; ; ){
//					i = random.nextInt(mList.size());
//						if (mList.get(i).excType.equals("1") && a!=i && b!=i) 
//						{
//							currLessonExercise.add(mList.get(i));
//							break;
//						}
//				}
//			}
//	}
//	
//	public List<LessonExercise> getCurLessonExercises(){
//		return currLessonExercise;
//	}
//	
//	public boolean hasFormer(){
//		
//		return hasFormer;
//	}
//	
//	public boolean hasNext(){
//		
//		return hasNext;
//	}
//	
//	public int getCurrQuestionId(){
//		//这部分主要判断是否有上一题、下一题，以后是否业务变化，这部分需要重写。现在只针对三个题
//		if (currQuestionId==0) {
//			hasFormer = false;
//			hasNext = true;
//		}
//		else if (currQuestionId == 1) {
//			hasFormer = true;
//			hasNext = true;
//		}
//		else if (currQuestionId == 2) {
//			hasFormer = true;
//			hasNext = false;
//		}
//		
//		return currQuestionId;
//	}
//	
//	public void setCurrQuestion(int i){
//		currQuestionId = i ;
//		getCurrQuestionId();
//	}
//}

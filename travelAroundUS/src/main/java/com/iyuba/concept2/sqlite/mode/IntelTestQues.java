package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/2.
 */
public class IntelTestQues implements Serializable {

    /**
     * TestType  测试的类型  0
     * 1(选择题）
     * 2(填空题）
     * 3(选择填空）
     * 4(图片选择）
     * 5(语音测评）
     * 6（多选）
     * 7（判断）
     * 8（单词拼写）
     */

    public int quesType=0;
    public String choiceA = "";
    public String choiceB = "";
    public String choiceC = "";
    public String choiceD = "";
    public String choiceE = "";
    public String category = "";
    public String image = "";
    public String testType = "";
    public String testId = "";
    public String sound = "";
    public String answer = "";
    public String question = "";
    public String id = "";
    public String tags = "";
    /** 加载的附件*/
    public String attach="";
}

package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取单词表数据库
 */
public class MultipleChoiceOp extends DatabaseService {
	public static final String TABLE_NAME = "Muti";//multiple_choice
	public static final String VOA_ID = "voa_id";
	public static final String Lesson = "lesson";
	public static final String Act = "act";
	public static final String Number = "number";
	public static final String QUESTION = "question";
	public static final String CHOICE_A = "ansA";
	public static final String CHOICE_B = "ansB";
	public static final String CHOICE_C = "ansC";
	public static final String CHOICE_D = "ansD";
	public static final String ANSWER = "answer";
	public static final String USER_ANSWER = "user_answer";

	public MultipleChoiceOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<MultipleChoice> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				MultipleChoice tempMultipleChoice = multipleChoices.get(i);
				importDatabase.openDatabase().execSQL(
					"UPDATE Muti SET \"user_answer\" = ? WHERE \"lesson\" = ? AND \"act\" = ? AND \"number\" = ?",
						new Object[] {
								tempMultipleChoice.userAnswer == null?"null":tempMultipleChoice.userAnswer,
								tempMultipleChoice.lesson,
								tempMultipleChoice.act,
								tempMultipleChoice.number
						});
				closeDatabase(null);
			}
		}
	}

	/**
	 * @return
	 */
	public synchronized List<MultipleChoice> findData(int voaId) {
		String sql  = "select " +Lesson+","+Act+","+Number+"," + VOA_ID + "," + QUESTION + "," + CHOICE_A + ","
				+ CHOICE_B + "," + CHOICE_C + "," + CHOICE_D + "," + ANSWER+","+USER_ANSWER
				+ " from " + TABLE_NAME
				+ " where " + VOA_ID + "=?";
		Cursor cursor = importDatabase.openDatabase().rawQuery(sql, new String[] {String.valueOf(voaId)});

		List<MultipleChoice> multChoiceList = new ArrayList<>();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			MultipleChoice multChoice = new MultipleChoice();
			multChoice.lesson = cursor.getInt(0);
			multChoice.act = cursor.getInt(1);
			multChoice.number = cursor.getInt(2);
			multChoice.voaId = cursor.getInt(3);
			multChoice.question = cursor.getString(4);
			multChoice.choiceA = cursor.getString(5);
			multChoice.choiceB = cursor.getString(6);
			multChoice.choiceC = cursor.getString(7);
			multChoice.choiceD = cursor.getString(8);
			multChoice.answer = tansformAnswer(cursor.getString(9));
			multChoice.userAnswer = cursor.getString(10);
			multChoiceList.add(multChoice);
		}


		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);

		return multChoiceList;
	}

	public String tansformAnswer(String answer) {
		String result = "";
		//数据库有问题
		if (answer.equals("B")){
			answer="2";
		}
		if (answer.equals("D")){
			answer="4";
		}
		switch(Integer.valueOf(answer)) {
		case 1:result = "a"; break;
		case 2:result = "b"; break;
		case 3:result = "c"; break;
		case 4:result = "d"; break;
		}

		return result;
	}
}

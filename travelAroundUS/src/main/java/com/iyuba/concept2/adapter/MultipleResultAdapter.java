package com.iyuba.concept2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.ExerciseRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 单选题答案页
 */
public class MultipleResultAdapter extends BaseAdapter {

    private Context mContext;
    private List<ExerciseRecord> mList = new ArrayList<ExerciseRecord>();
    public boolean modeDelete = false;
    public ViewHolder viewHolder;

    public MultipleResultAdapter(Context context, List<ExerciseRecord> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ExerciseRecord exerciseRecord = mList.get(position);
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.item_multiple_result, null);

            viewHolder = new ViewHolder();
            viewHolder.tv_questionid = (TextView) convertView.findViewById(R.id.tv_questionid);
            viewHolder.tv_myanswer = (TextView) convertView.findViewById(R.id.tv_myanswer);
            viewHolder.tv_rightanswer = (TextView) convertView.findViewById(R.id.tv_rightanswer);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_questionid.setText(exerciseRecord.TestNumber + ".我的答案:");
        viewHolder.tv_myanswer.setText(exerciseRecord.UserAnswer);
        viewHolder.tv_rightanswer.setText(exerciseRecord.RightAnswer);

        if (exerciseRecord.AnswerResut == 1) {

            viewHolder.tv_myanswer.setTextColor(Color.parseColor("#0077f9"));
            /*F6B476*/

        } else {
            viewHolder.tv_myanswer.setTextColor(Color.parseColor("#4BC894"));

        }

        return convertView;
    }

    public class ViewHolder {
        TextView tv_questionid;
        TextView tv_myanswer;
        TextView tv_rightanswer;
    }


}

package com.iyuba.concept2.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.SummaryBean;
import com.iyuba.concept2.sqlite.op.SummaryOp;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private TextView text;
    private int voaId;
    private List<SummaryBean> summaryLanguageBeans;
    private SummaryOp summaryLanguageOp=new SummaryOp(getActivity());


    public SummaryFragment() {
        // Required empty public constructor
    }
    public static SummaryFragment newInstance(int artId) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("artId", artId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        voaId = bundle.getInt("artId");
        View view=inflater.inflate(R.layout.fragment_summary, container, false);
        text=view.findViewById(R.id.tv_summary);
        return view;
    }
    private void initVoaAnnotation() {
        summaryLanguageBeans = summaryLanguageOp.findDataByVoaId(voaId);
        if (summaryLanguageBeans.size() == 0) {
           // noAnnotationView.setVisibility(View.VISIBLE);
           // annoListView.setVisibility(View.GONE);
        } else {
          //  annoListView.setVisibility(View.VISIBLE);
            //noAnnotationView.setVisibility(View.GONE);
            String note = summaryLanguageBeans.get(0).sentence;
            SpannableStringBuilder style = transformString(note);
            Log.e("note:",note);
            text.setText(note);
        }

    }

    public SpannableStringBuilder transformString(String str) {
        String[] strs = str.split("\\+\\+\\+");
        str = str.replaceAll("\\+\\+\\+", "");
        int from = 0;
        int to = 0;

        SpannableStringBuilder style = new SpannableStringBuilder(str);

        if (strs.length > 1) {
            for (int i = 0; i < strs.length - 2; i = i + 2) {
                from += strs[i].length();
                to = from + strs[i + 1].length();
                style.setSpan(new StyleSpan(
                                android.graphics.Typeface.BOLD_ITALIC), from, to,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                from += strs[i + 1].length();
            }
        }

        return style;
    }

    public void onResume() {
        initVoaAnnotation();
        super.onResume();
    }
}

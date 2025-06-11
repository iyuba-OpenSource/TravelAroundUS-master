package com.iyuba.concept2.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.SummaryLanguageAdapter;
import com.iyuba.concept2.sqlite.mode.VoaSummaryLanguageBean;
import com.iyuba.concept2.sqlite.op.SummaryLanguageOp;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageFragment extends Fragment {

    private ListView annoListView;
    private View noAnnotationView;
    private List<VoaSummaryLanguageBean> summaryLanguageBeans;
    private SummaryLanguageOp summaryLanguageOp=new SummaryLanguageOp(getActivity());
    private SummaryLanguageAdapter annosAdapter;

    private int voaId;

    public LanguageFragment() {
        // Required empty public constructor
    }

    public static LanguageFragment newInstance(int artId) {
        LanguageFragment fragment = new LanguageFragment();
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
        View view=inflater.inflate(R.layout.fragment_langeuage, container, false);
        init(view);
        return view;
    }

    public void init(View container) {
        noAnnotationView = container.findViewById(R.id.no_annotation_view);
        annoListView = container.findViewById(R.id.annotation_list);
    }

    private void initVoaAnnotation() {
        summaryLanguageBeans = summaryLanguageOp.findDataByVoaId(voaId);
        if (summaryLanguageBeans.size() == 0) {
                noAnnotationView.setVisibility(View.VISIBLE);
                annoListView.setVisibility(View.GONE);
            } else {
                annoListView.setVisibility(View.VISIBLE);
                noAnnotationView.setVisibility(View.GONE);
                annosAdapter = new SummaryLanguageAdapter(getActivity(), (ArrayList<VoaSummaryLanguageBean>) summaryLanguageBeans);
                annoListView.setAdapter(annosAdapter);
            }

    }
    public void onResume() {
        initVoaAnnotation();
        super.onResume();
    }
}

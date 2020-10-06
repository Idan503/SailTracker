package com.idan_koren_israeli.sailtracker.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;


public class LoadingFragment extends Fragment {

    View parent;
    TextView textView;


    public LoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.parent = inflater.inflate(R.layout.fragment_loading, container, false);
        findViews();
        hide();
        return parent;
    }

    private void findViews(){
        textView = parent.findViewById(R.id.loading_LBL_text);
    }

    public void show(){
        parent.setVisibility(View.VISIBLE);
    }

    public void hide(){
        parent.setVisibility(View.GONE);
    }

    public void setText(String text){
        textView.setText(text);
    }

}
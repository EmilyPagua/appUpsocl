package com.upsocl.appupsocl.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upsocl.appupsocl.R;

public class HelpFragment extends Fragment {


    public HelpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceStates){
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        return root;
    }
}

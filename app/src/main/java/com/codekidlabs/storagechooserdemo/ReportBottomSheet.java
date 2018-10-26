package com.codekidlabs.storagechooserdemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codekidlabs.storagechooserdemo.utils.TypefaceUtil;

/**
 * report any issue using logboard api
 */

public class ReportBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_sheet_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v = getView();

        TextView reportText = null;
        if (v != null) {
            reportText = v.findViewById(R.id.report_text);
        }
        TypefaceUtil.setTypefaceBold(getContext(), reportText);

        Button helpButton = v.findViewById(R.id.help_button);
        TypefaceUtil.setTypefaceLight(getContext(), helpButton);
        helpButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

//        storageChooserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        Button reportButton = v.findViewById(R.id.report_button);
        TypefaceUtil.setTypefaceLight(getContext(), reportButton);
        reportButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
//
//        storageChooserButtonMemorybar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}

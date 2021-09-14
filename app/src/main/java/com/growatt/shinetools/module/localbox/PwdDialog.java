package com.growatt.shinetools.module.localbox;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;

/**
 * Created：2017/12/8 on 20:12
 * Author:gaideng on dg
 * Description:
 */

public class PwdDialog extends DialogFragment {
    private TextView tvTitle;
    private TextView tvContent;
    private TextView mPassword;
    private Button btnOk;
    private Button btnNo;
    private AlertDialog mDialog;
    public PwdDialog() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_password, null);
        mPassword = (EditText) view.findViewById(R.id.etContent);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        btnNo = (Button) view.findViewById(R.id.btnNo);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        builder.setView(view);

        return mDialog = builder.create();
    }
}

package com.mobica.speedlock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentSettings extends DialogFragment {

    private static final String SPEED_SELECTED_KEY = "SPEED_SELECTED_KEY";
    private static final String SPEED_UNIT = "KM/H";

    private SpeedHandler mSpeedHandler;
    private TextView mSpeedTextView;
    private ImageButton mBtnNext;
    private ImageButton mBtnPrev;
    private ImageButton mBtnExit;
    private Button mSetData;
    private int mSpeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mBtnNext = v.findViewById(R.id.button_FragmentSettings_Next);
        mBtnPrev = v.findViewById(R.id.button_FragmentSettings_Previous);
        mBtnExit = v.findViewById(R.id.button_FragmentSettings_Exit);
        mSetData = v.findViewById(R.id.button_FragmentSettings_Set);
        mSpeedTextView = v.findViewById(R.id.textView_FragmentSettings_SpeedInfo);

        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeedHandler.setLastSpeed();
            }
        });

        mSetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeedHandler.setNewSpeed(mSpeed);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpeed < 30) {
                    mSpeed += 5;
                    mSpeedTextView.setText(mSpeed + SPEED_UNIT);
                }
            }
        });

        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpeed > 5) {
                    mSpeed -= 5;
                    mSpeedTextView.setText(mSpeed + SPEED_UNIT);

                }
            }
        });

        Bundle bundle = getArguments();
        if (null != bundle) {
            int mSpeedBundle = bundle.getInt(SPEED_SELECTED_KEY, 0);
            mSpeedTextView.setText(mSpeedBundle + SPEED_UNIT);
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSpeedHandler = (SpeedHandler) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSpeedHandler = null;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
        mSpeedTextView.setText(mSpeed + SPEED_UNIT);
    }

    public interface SpeedHandler {
        void setLastSpeed();

        void setNewSpeed(int newSpeedSelected);
    }
}
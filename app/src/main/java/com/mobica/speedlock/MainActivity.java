package com.mobica.speedlock;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mobica.speedlock.model.Hit;
import com.mobica.speedlock.model.HitsList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FragmentSettings.SpeedHandler {

    private static final String TAG = MainActivity.class.getName();
    private static final String PREF_KEY = "MYPREFERENCES";
    private static final String SPEED_SELECTED_KEY = "SELECTED_SPEED";
    private static final int DEFAULT_SPEED = 10;

    private static final int CAN_ID_112_SPEED = 291504647;
    private static final int CAN_DEFAULT_RATE = 1;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsAdapter mNewsAdapter;
    private List<Hit> mHits = new ArrayList<>();
    private RecyclerViewDisabler mDisabler;
    private boolean mIsSpeedlockEnabled;
    private FragmentSettings mFragment;
    private FragmentTransaction mFragmentTransaction;
    private SharedPreferences mPreferences;
    private TextView mTxtError;
    private ProgressBar mProgressBar;
    private int mSpeedPref;

    private Car mCarApi;
    private CarPropertyManager mPropertyManager;

    private final ServiceConnection mCarServiceConnectionCallback = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                mPropertyManager = (CarPropertyManager) mCarApi.getCarManager(android.car.Car.PROPERTY_SERVICE);
                mPropertyManager.registerListener(mCarPropertyListener, CAN_ID_112_SPEED, CAN_DEFAULT_RATE);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mPropertyManager.unregisterListener(mCarPropertyListener);
        }
    };

    private CarPropertyManager.CarPropertyEventListener mCarPropertyListener = new CarPropertyManager.CarPropertyEventListener() {
        @Override
        public void onChangeEvent(CarPropertyValue value) {
            if (value.getPropertyId() == CAN_ID_112_SPEED) {
                float canValue = (float) value.getValue();

                if (canValue >= mSpeedPref) {
                    enableSpeedLock();
                } else {
                    disableSpeedLock();
                }
            }
        }

        @Override
        public void onErrorEvent(int propId, int zone) {
            //no-op for now
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFragment();

        mLayoutManager = new LinearLayoutManager(MainActivity.this);

        mProgressBar = findViewById(R.id.progressBar_Main);
        mTxtError = findViewById(R.id.textView_Main_Error);

        ImageButton mSettingsBtn = findViewById(R.id.button_Main_Settings);
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFragmentVisibility();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView_Main);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mDisabler = new RecyclerViewDisabler();

        init();
        loadPreferences(getApplicationContext());

        mCarApi = Car.createCar(this, mCarServiceConnectionCallback);
        mCarApi.connect();
    }

    public void loadPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mSpeedPref = mPreferences.getInt(SPEED_SELECTED_KEY, DEFAULT_SPEED);
    }

    public void createFragment() {
        mFragment = new FragmentSettings();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.container, mFragment);
        mFragmentTransaction.hide(mFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void setLastSpeed() {
        hideSettings();
    }

    @Override
    public void setNewSpeed(int newSpeedSelected) {
        mSpeedPref = newSpeedSelected;
        mPreferences.edit().putInt(SPEED_SELECTED_KEY, mSpeedPref).apply();
        hideSettings();
    }

    public void showSettings() {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.show(mFragment);
        mFragmentTransaction.commit();
        mFragment.setSpeed(mSpeedPref);
    }

    public void hideSettings() {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.hide(mFragment);
        mFragmentTransaction.commit();
    }

    public void toggleFragmentVisibility() {
        if (mFragment == null) {
            return;
        }
        if (mFragment.isHidden()) {
            showSettings();
        } else {
            hideSettings();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Handler handler = new Handler();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mIsSpeedlockEnabled && !mHits.isEmpty()) {
                        disableSpeedLock();
                        enableSpeedLock();
                    } else if (!mHits.isEmpty()) {
                        disableSpeedLock();
                    }
                }
            }, 10);
        }
    }

    public void enableSpeedLock() {
        if (!mIsSpeedlockEnabled && !mHits.isEmpty()) {
            mRecyclerView.stopScroll();
            int findFirst = mLayoutManager.findFirstVisibleItemPosition();
            mRecyclerView.smoothScrollToPosition(findFirst);
            mRecyclerView.addOnItemTouchListener(mDisabler);
            findFirst = mLayoutManager.findFirstVisibleItemPosition();
            int findLast = mLayoutManager.findLastCompletelyVisibleItemPosition();
            int center = findLast - (findLast - findFirst) / 2;
            mNewsAdapter.setVisible(center, false);
            mIsSpeedlockEnabled = true;
            mNewsAdapter.notifyDataSetChanged();
        }
    }

    public void disableSpeedLock() {
        if (mIsSpeedlockEnabled && !mHits.isEmpty()) {
            mRecyclerView.removeOnItemTouchListener(mDisabler);
            mIsSpeedlockEnabled = false;
            mNewsAdapter.setVisible(0, true);
            mNewsAdapter.notifyDataSetChanged();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(keyCode == event.KEYCODE_A){
             enableSpeedLock();
         } else if (keyCode == event.KEYCODE_B){
             disableSpeedLock();
         }
        return super.onKeyDown(keyCode, event);
    }

    public void init() {
        mNewsAdapter = new NewsAdapter(getHitsData(), MainActivity.this);
        mHits = getHitsData();
        mRecyclerView.setAdapter(mNewsAdapter);
        mProgressBar.setVisibility(View.GONE);
    }

    public List<Hit> getHitsData() {
        String jsonString = getAssetsJSON("news.json");
        Log.d(TAG, "Json: " + jsonString);
        Gson gson = new Gson();
        HitsList hitsList = gson.fromJson(jsonString, HitsList.class);
        return  hitsList.getHits();
    }

    public String getAssetsJSON(String fileName) {
        String json = null;
        try {
            InputStream inputStream = this.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}


/* LOCAL API
    public void initRecyclerView(HitsList mHits) {
        mNewsAdapter = new NewsAdapter(mHits.hitList, MainActivity.this);
        recyclerView.setAdapter(mNewsAdapter);
    }

    public HitsList loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("news.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.e("data", json);
        Gson gson = new Gson();
        HitsList mHits = gson.fromJson(json, HitsList.class);
        return mHits;
    }
    public void LoadJson() {
        if (!mHits.isEmpty()) {
            mHits.clear();
        }
        loadJSONFromAsset(this)
        initRecyclerView();
        mNewsAdapter.notifyDataSetChanged();
    }

  */
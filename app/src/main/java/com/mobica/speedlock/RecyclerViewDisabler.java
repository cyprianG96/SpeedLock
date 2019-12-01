package com.mobica.speedlock;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //Do nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //Do nothing
    }
}
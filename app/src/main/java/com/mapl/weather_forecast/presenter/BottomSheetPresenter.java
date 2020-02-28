package com.mapl.weather_forecast.presenter;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapl.weather_forecast.view.BottomSheetView;

import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class BottomSheetPresenter extends MvpPresenter<BottomSheetView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @Override
    public void attachView(BottomSheetView view) {
        super.attachView(view);
    }

    public void topBarClick(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                getViewState().setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                getViewState().setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    public void bottomSheetSlide(float slideOffset) {
        if (slideOffset > 0)
            getViewState().clearFocus();
    }

    public void bottomSheetStateChanged(int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                getViewState().bottomSheetExpanded();
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                getViewState().bottomSheetCollapsed();
                break;
        }
    }

    public void backPressed(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_EXPANDED:
                getViewState().closeBottomSheet();
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                getViewState().closeSearchActivity();
                break;
        }
    }
}
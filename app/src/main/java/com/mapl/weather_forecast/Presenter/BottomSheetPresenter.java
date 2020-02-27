package com.mapl.weather_forecast.Presenter;

import android.app.Activity;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.mapl.weather_forecast.Model.BottomSheetModel;
import com.mapl.weather_forecast.View.BottomSheetView;

import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class BottomSheetPresenter extends MvpPresenter<BottomSheetView> {
    private BottomSheetModel model;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        model = new BottomSheetModel();
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

    public void fabLocationClick() {
        /*model.getLocation();
        model.setLocation();*/
    }

    public void fabDoneClick(Activity activity, View dialog, TextInputEditText inputEditText) {
        MaterialAlertDialogBuilder alertDialog = model.getDialog(activity, dialog, inputEditText);
        getViewState().setDialog(alertDialog);
    }

    public void bottomSheetSlide(float slideOffset) {
        //model.clearFocus(slideOffset);
    }

    public void bottomSheetStateChanged(int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                //model.bottomSheetExpanded();
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                //model.bottomSheetCollapsed();
                break;
        }
    }
}
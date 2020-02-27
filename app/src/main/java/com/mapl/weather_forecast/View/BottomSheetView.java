package com.mapl.weather_forecast.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndStrategy.class)
public interface BottomSheetView extends MvpView {
    void setBottomSheetState(int state);
    void setDialog(MaterialAlertDialogBuilder alertDialog);
}
package com.mapl.weather_forecast.view;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndStrategy.class)
public interface BottomSheetView extends MvpView {
    void setBottomSheetState(int state);
    void setLocation();
    void setDialog();
    void clearFocus();
    void bottomSheetExpanded();
    void bottomSheetCollapsed();
    void closeBottomSheet();
    void closeSearchActivity();
}
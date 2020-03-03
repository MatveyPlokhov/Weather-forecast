package com.mapl.weather_forecast.view;

import com.mapl.weather_forecast.adapter.CityDataClassSearchPage;

import java.util.ArrayList;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndStrategy.class)
public interface SearchActivityView  extends MvpView {
    void setRecyclerView(ArrayList<CityDataClassSearchPage> arrayList);
}

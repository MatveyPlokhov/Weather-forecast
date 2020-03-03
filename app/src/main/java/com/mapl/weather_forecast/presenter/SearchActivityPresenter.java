package com.mapl.weather_forecast.presenter;

import android.annotation.SuppressLint;

import com.mapl.weather_forecast.adapter.CityDataClassSearchPage;
import com.mapl.weather_forecast.model.SearchActivityModel;
import com.mapl.weather_forecast.view.SearchActivityView;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import moxy.MvpPresenter;

@SuppressLint("CheckResult")
public class SearchActivityPresenter extends MvpPresenter<SearchActivityView> {
    private SearchActivityModel model;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        model = new SearchActivityModel();
    }

    @Override
    public void attachView(SearchActivityView view) {
        super.attachView(view);
    }

    public void textChanged(String text) {
        Observable.create((ObservableOnSubscribe<ArrayList<CityDataClassSearchPage>>) emitter -> {
            JSONObject jsonObject = model.getJSONData(text);
            ArrayList<CityDataClassSearchPage> arrayList = (jsonObject != null) ? model.cityList(jsonObject) : new ArrayList<>();
            emitter.onNext(arrayList);
        }).subscribe(arrayList -> getViewState().setRecyclerView(arrayList));
    }
}

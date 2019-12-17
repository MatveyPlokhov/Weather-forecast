package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SearchCityCard extends Fragment {
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private Activity activity;
    private String[] cities;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cities_fragment, container, false);
        initView(rootView);
        addFragments();
        return rootView;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void addFragments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, false);
        cities = getResources().getStringArray(R.array.citiesArray);
        ArrayList<CityDataClass> arrayList = new ArrayList<>();

        for (String city : cities)
            arrayList.add(new CityDataClass(city));

        recyclerViewAdapter = new RecyclerViewAdapter(arrayList,activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}

package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.LinkedList;

public class CitiesFragment extends Fragment implements Postman {
    LinkedList<String> cities;
    LinearLayout verticalLinearLayoutCities;
    Activity activity;

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
        addCitiesButtons();
        return rootView;
    }

    private void initView(View view) {
        cities = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.citiesArray)));
        verticalLinearLayoutCities = view.findViewById(R.id.verticalLinearLayoutCities);
    }

    private void addCitiesButtons() {
        for (int i = 0; i < cities.size(); i++) {
            final Button button = new Button(getContext());
            button.setLayoutParams(new ViewGroup.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText(cities.get(i));
            verticalLinearLayoutCities.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((Postman) activity).fragmentMail((String) button.getText());
                    } catch (ClassCastException ignored) {
                    }
                }
            });
        }
    }

    @Override
    public void fragmentMail(String cityName) {
    }
}

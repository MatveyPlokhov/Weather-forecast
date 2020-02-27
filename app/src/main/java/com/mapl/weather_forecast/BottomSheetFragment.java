package com.mapl.weather_forecast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mapl.weather_forecast.Presenter.BottomSheetPresenter;
import com.mapl.weather_forecast.View.BottomSheetView;

import moxy.MvpBottomSheetDialogFragment;
import moxy.presenter.InjectPresenter;

public class BottomSheetFragment extends MvpBottomSheetDialogFragment
        implements BottomSheetView, View.OnClickListener {

    @InjectPresenter
    BottomSheetPresenter presenter;

    private final Handler handler = new Handler();
    private Activity activity;
    private MapView mapView;
    private GoogleMap googleMap;
    private FloatingActionButton fabDone, fabLocation;
    private OnMapReadyCallback onMapReadyCallback;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;
    private LatLng center;
    private MaterialCardView topBar;
    private ImageView location, pointer;

    private boolean notRunBefore = true;

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bottom,container,false);

        mapView = root.findViewById(R.id.bigMapView);
        location = root.findViewById(R.id.locationImage);
        pointer = root.findViewById(R.id.pointerImageView);
        bottomSheet = BottomSheetBehavior.from(root.findViewById(R.id.bottom_sheet));
        topBar = root.findViewById(R.id.topBar);
        fabLocation = root.findViewById(R.id.fabLocation);
        fabDone = root.findViewById(R.id.fabDone);

        bottomSheet.addBottomSheetCallback(bottomSheetCallback);
        topBar.setOnClickListener(this);
        fabLocation.setOnClickListener(this);
        fabDone.setOnClickListener(this);

        return root;
    }

    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            presenter.bottomSheetStateChanged(newState);
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            presenter.bottomSheetSlide(slideOffset);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.topBar:
                int state = bottomSheet.getState();
                presenter.topBarClick(state);
                break;
            case R.id.fabLocation:
                presenter.fabLocationClick();
                break;
            case R.id.fabDone:
                @SuppressLint("InflateParams")
                View dialog = LayoutInflater.from(activity).inflate(R.layout.textinputlayout_item, null);
                TextInputEditText inputEditText = dialog.findViewById(R.id.textInputEditText);
                presenter.fabDoneClick(activity, dialog, inputEditText);
                break;
        }
    }

    @Override
    public void setBottomSheetState(int state) {
        bottomSheet.setState(state);
    }

    @Override
    public void setDialog(MaterialAlertDialogBuilder alertDialog) {
        alertDialog.show();
    }
}
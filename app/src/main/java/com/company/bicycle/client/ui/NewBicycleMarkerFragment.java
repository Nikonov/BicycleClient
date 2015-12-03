package com.company.bicycle.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.company.bicycle.client.R;
import com.company.bicycle.client.modal.BicycleMarker;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by andrey on 03.12.15.
 */
public class NewBicycleMarkerFragment extends Fragment {
    private static final String EXTRA_MARKER = "extra_marker";
    private EditText mFind;
    private EditText mDescription;
    private TextView mLatitude;
    private TextView mLongitude;
    private View mAddNewMarker;
    private OnNewMarker mCallback;

    public interface OnNewMarker {
        void onAddNewMarker(BicycleMarker marker);
    }

    public static NewBicycleMarkerFragment newInstance(LatLng position) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MARKER, position);
        NewBicycleMarkerFragment fragment = new NewBicycleMarkerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_bicycle_marker, container, false);
        mFind = (EditText) rootView.findViewById(R.id.find_edit_marker);
        mDescription = (EditText) rootView.findViewById(R.id.description_edit_marker);
        mLatitude = (TextView) rootView.findViewById(R.id.marker_position_latitude);
        mLongitude = (TextView) rootView.findViewById(R.id.marker_position_longitude);
        mAddNewMarker = rootView.findViewById(R.id.add_new_marker_btn);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LatLng position = getArguments().getParcelable(EXTRA_MARKER);
        updateUI(position);
        mAddNewMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    BicycleMarker marker = new BicycleMarker();
                    String findText = mFind.getText() == null ? "" : mFind.getText().toString();
                    marker.setFindDescription(findText);
                    String descriptionText = mDescription.getText() == null ? "" : mDescription.getText().toString();
                    marker.setDescription(descriptionText);
                    if (position != null) {
                        marker.setLatitude(position.latitude);
                        marker.setLongitude(position.longitude);
                    }
                    mCallback.onAddNewMarker(marker);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnNewMarker) {
            mCallback = (OnNewMarker) getActivity();
        }
    }

    private void updateUI(LatLng position) {
        if (position == null) return;
        if (isAdded()) {
            //position
            String latitudeFormat = getString(R.string.latitude_marker);
            mLatitude.setText(String.format(latitudeFormat, position.latitude));
            String longitudeFormat = getString(R.string.longitude_marker);
            mLongitude.setText(String.format(longitudeFormat, position.longitude));
        }

    }

}

package com.company.bicycle.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.bicycle.client.R;
import com.company.bicycle.client.modal.BicycleMarker;

/**
 * Created by andrey on 03.12.15.
 */
public class DescriptionMarkerFragment extends Fragment {
    private static final String EXTRA_MARKER = "extra_marker";
    private TextView mTitle;
    private TextView mFind;
    private TextView mDistance;
    private TextView mLatitude;
    private TextView mLongitude;

    public static DescriptionMarkerFragment newInstance(BicycleMarker marker) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MARKER, marker);
        DescriptionMarkerFragment fragment = new DescriptionMarkerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description_marker, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.title_marker);
        mFind = (TextView) rootView.findViewById(R.id.find_marker);
        mDistance = (TextView) rootView.findViewById(R.id.distance_marker);
        mLatitude = (TextView) rootView.findViewById(R.id.marker_position_latitude);
        mLongitude = (TextView) rootView.findViewById(R.id.marker_position_longitude);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BicycleMarker marker = getArguments().getParcelable(EXTRA_MARKER);
        updateUI(marker);

    }

    private void updateUI(BicycleMarker marker) {
        if (marker == null) return;
        if (isAdded()) {
            //title
            String titleFormat = getString(R.string.title_marker_bicycle_parking);
            mTitle.setText(String.format(titleFormat, marker.getId()));
            //how find
            final String findInfo = marker.getFindDescription();
            if (TextUtils.isEmpty(findInfo)) {
                mFind.setText(R.string.find_empty);
            } else {
                mFind.setText(marker.getFindDescription());
            }
            //distance
            String distanceFormat = getString(R.string.distance_marker);
            mDistance.setText(String.format(distanceFormat, marker.getDistance()));
            //position
            String latitudeFormat = getString(R.string.latitude_marker);
            mLatitude.setText(String.format(latitudeFormat, marker.getLatitude()));
            String longitudeFormat = getString(R.string.longitude_marker);
            mLongitude.setText(String.format(longitudeFormat, marker.getLongitude()));
        }

    }
}

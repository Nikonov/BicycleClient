package com.company.bicycle.client.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.bicycle.client.R;
import com.company.bicycle.client.modal.BicycleMarker;

import java.util.ArrayList;
import java.util.List;
import static com.company.bicycle.client.utils.Logger.*;
/**
 * Created by andrey on 03.12.15.
 */
public class DescriptionContainerFragment extends Fragment {
    private static final String LOG_DEBUG = makeLogTag(DescriptionContainerFragment.class);
    private static final int OFF_SCREEN_PAGES = 2;
    private static final String EXTRA_ALL_MARKERS = "extra_all_markers";
    private static final String EXTRA_CURRENT_POSITION = "extra_current_position";
    private ViewPager mMarkersPages;


    public static DescriptionContainerFragment newInstance(ArrayList<BicycleMarker> allMarkers, int positionCurrent) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_ALL_MARKERS, allMarkers);
        bundle.putInt(EXTRA_CURRENT_POSITION, positionCurrent);
        DescriptionContainerFragment fragment = new DescriptionContainerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description_container, container, false);
        mMarkersPages = (ViewPager) rootView.findViewById(R.id.description_pager);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMarkersPages.setOffscreenPageLimit(OFF_SCREEN_PAGES);
        ArrayList<BicycleMarker> markers = getArguments().getParcelableArrayList(EXTRA_ALL_MARKERS);
        int currentPosition = getArguments().getInt(EXTRA_CURRENT_POSITION);
        logDebug(LOG_DEBUG, " currentPosition: " + currentPosition + " all markers size: " + markers.size());
        MarkersAdapter adapter = new MarkersAdapter(getChildFragmentManager(), markers);
        mMarkersPages.setAdapter(adapter);
        mMarkersPages.setCurrentItem(currentPosition);
    }

    public void updateData(ArrayList<BicycleMarker> allMarkers, int positionCurrent){

    }

    private class MarkersAdapter extends FragmentStatePagerAdapter {
        private List<BicycleMarker> mMarkers;

        public MarkersAdapter(FragmentManager fm, List<BicycleMarker> markers) {
            super(fm);
            mMarkers = markers;
            if (mMarkers == null) {
                mMarkers = new ArrayList<>();
            }
        }

        @Override
        public Fragment getItem(int position) {
            BicycleMarker marker = mMarkers.get(position);
            return DescriptionMarkerFragment.newInstance(marker);
        }

        @Override
        public int getCount() {
            return mMarkers.size();
        }
    }
}

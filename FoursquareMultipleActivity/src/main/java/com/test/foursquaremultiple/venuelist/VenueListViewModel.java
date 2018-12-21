package com.test.foursquaremultiple.venuelist;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.test.foursquaremultiple.model.Venue;

import java.util.List;

/**
 * ViewModel to get data from bundle and dispatch data to RecyclerView
 */
public class VenueListViewModel extends ViewModel {

    /**
     * Wrapper class for data fetch status
     */
    private MutableLiveData<List<Venue>> mVenueListData;


    public MutableLiveData<List<Venue>> getVenueListLiveData() {
        return mVenueListData;
    }

}

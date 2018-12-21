
package com.test.foursquaremultiple.di;

import com.test.foursquaremultiple.venuelist.VenueListFragment;
import com.test.foursquaremultiple.venuesearch.VenueSearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;



@Module
public abstract class ListFragmentContributorModule {

    @ContributesAndroidInjector
    abstract VenueSearchFragment contributeVenueSearchFragment();

    @ContributesAndroidInjector
    abstract VenueListFragment contributeVenueListFragment();
}


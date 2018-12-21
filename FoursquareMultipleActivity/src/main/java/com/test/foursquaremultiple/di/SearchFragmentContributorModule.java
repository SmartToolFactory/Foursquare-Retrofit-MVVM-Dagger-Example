
package com.test.foursquaremultiple.di;

import com.test.foursquaremultiple.venuelist.VenueListFragment;
import com.test.foursquaremultiple.venuesearch.VenueSearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


/**
 * SearchFragmentContributorModule is used inside ActivityContributorModule
 * With @ContributesAndroidInjector(modules = MyFragmentModule.class)
 * defines which module will be used to inject objects to declared fragments
 */
@Module
public abstract class SearchFragmentContributorModule {

    @ContributesAndroidInjector
    abstract VenueSearchFragment contributeVenueSearchFragment();

    @ContributesAndroidInjector
    abstract VenueListFragment contributeVenueListFragment();
}


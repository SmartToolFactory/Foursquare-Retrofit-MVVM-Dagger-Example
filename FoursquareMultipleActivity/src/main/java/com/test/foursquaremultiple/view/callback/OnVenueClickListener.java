package com.test.foursquaremultiple.view.callback;

import com.test.foursquaremultiple.model.Venue;
import com.test.foursquaremultiple.venuelist.VenueAdapter;

/**
 * Callback for {@link VenueAdapter} on item click to display details of Venue
 */
public interface OnVenueClickListener {
    void onClick(Venue venue);
}

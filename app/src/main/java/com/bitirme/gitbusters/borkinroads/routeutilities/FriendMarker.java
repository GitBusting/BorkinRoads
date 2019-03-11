package com.bitirme.gitbusters.borkinroads.routeutilities;

import com.google.android.gms.maps.model.LatLng;

public class FriendMarker {

  private LatLng position;

  public FriendMarker(LatLng ll)
  {
    position = ll;
  }

  public LatLng getPosition()
  {
    return position;
  }

}

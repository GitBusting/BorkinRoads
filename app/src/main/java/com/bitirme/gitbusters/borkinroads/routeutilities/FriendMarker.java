package com.bitirme.gitbusters.borkinroads.routeutilities;

import com.google.android.gms.maps.model.LatLng;

public class FriendMarker {

  private LatLng position;
  private String name;

  public FriendMarker(LatLng ll, String name)
  {
    position = ll;
    this.name = name;
  }

  public LatLng getPosition()
  {
    return position;
  }

  public String getName() { return name; }

}

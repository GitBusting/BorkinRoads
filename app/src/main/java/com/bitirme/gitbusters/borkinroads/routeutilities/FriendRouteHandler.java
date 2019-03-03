package com.bitirme.gitbusters.borkinroads.routeutilities;

import android.os.CountDownTimer;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.bitirme.gitbusters.borkinroads.MapActivity;
import com.bitirme.gitbusters.borkinroads.data.PublicRouteRecord;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPuller;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Responsible for displaying and updating routes
 * which includes displaying friend routes as well
 * in a fashionable way.
 */
public class FriendRouteHandler extends Thread implements DirectionCallback {

  /* To be assigned when MapActivity is created */
  private static MapActivity mapReference;

  private ArrayList<PublicRouteRecord> activeRoutes;

  private CountDownTimer cdt;

  public FriendRouteHandler()
  {
    super();
    activeRoutes = new ArrayList<>();
  }

  public static void setMapReference(MapActivity mapref)
  {
    mapReference = mapref;
  }

  @Override
  public void run()
  {
    cdt = new CountDownTimer(20000, 10000) {
      public void onTick(long millisUntilFinished) {
        System.out.println("FriendRouteHandler: timer heartbeat");
      }
      public void onFinish() {
        RestPuller rp = new RestPuller(new PublicRouteRecord());
        rp.start();
        // Get new records from the database
        ArrayList<PublicRouteRecord> newRecords = new ArrayList<>();
        for(RestRecordImpl rri : rp.getFetchedRecords())
          newRecords.add((PublicRouteRecord) rri);
        Collections.sort(newRecords);

        // Compare newly fetched routes with already existing ones
        // if there exists a new route we tell the MapActivity to display it
        if(activeRoutes.size() != newRecords.size())
        {
          for (PublicRouteRecord prr : newRecords)
          {
            boolean exists = false;
            for (PublicRouteRecord act : activeRoutes)
            {
              if(act.equals(prr))
              {
                exists = true;
                break;
              }
            }
            if(!exists)
            {
              ArrayList<LatLng> ll = prr.getWaypoints();
              requestDirection(ll.remove(0), ll.remove(ll.size()-1),ll);
              activeRoutes.add(prr);
            }
          }
        }

        // TODO update friend indicators on map
        ArrayList<FriendMarker> fms = new ArrayList<>();
        for(PublicRouteRecord prr : activeRoutes)
          fms.add(new FriendMarker(prr.getLocation()));
        mapReference.displayFriendMarkers(fms);
      }
    }.start();
  }

  private void requestDirection(LatLng start, LatLng end, ArrayList<LatLng> coords) {
    GoogleDirection.withServerKey(mapReference.getApiKey())
            .from(start)
            .and(coords)
            .to(end)
            .transportMode(TransportMode.WALKING)
            .execute(this);
  }

  @Override
  public void onDirectionSuccess(Direction direction, String rawBody) {
      mapReference.displayFriendRoute(direction);
  }

  @Override
  public void onDirectionFailure(Throwable t) {

  }
}

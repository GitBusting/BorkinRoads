package com.bitirme.gitbusters.borkinroads.routeutilities;

import android.os.CountDownTimer;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.bitirme.gitbusters.borkinroads.MapActivity;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;
import com.bitirme.gitbusters.borkinroads.data.UserStatusRecord;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPuller;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for displaying and updating routes
 * which includes displaying friend routes as well
 * in a fashionable way.
 */
public class FriendRouteHandler extends Thread implements DirectionCallback {

  private static final boolean SANDBOX = true;
  private static final LatLng SBOX_START = new LatLng(39.9185,32.8562983);
  private static final LatLng SBOX_END = new LatLng(39.9185,32.8562983);
  private static final ArrayList<LatLng> SBOX_WAYPS = new ArrayList<>();

  /* To be assigned when MapActivity is created */
  private static MapActivity mapReference;

  private ArrayList<UserStatusRecord> activeRoutes;

  private CountDownTimer cdt;

  public FriendRouteHandler()
  {
    super();
    activeRoutes = new ArrayList<>();
    if(SANDBOX)
    {
      SBOX_WAYPS.add(new LatLng(39.91893273133,32.860237509));
      SBOX_WAYPS.add(new LatLng(39.92218008905,32.8554963693));
      SBOX_WAYPS.add(new LatLng(39.9202369,32.8495951741));
      SBOX_WAYPS.add(new LatLng(39.9165384488,32.852128520));
    }

  }

  public static void setMapReference(MapActivity mapref)
  {
    mapReference = mapref;
  }

  @Override
  public void run()
  {
    if(SANDBOX)
      Logger.getGlobal().log(Level.WARNING, "FriendRouteHandler is working in SANDBOX mode!");
    frhLoop();
  }

  private final void frhLoop()
  {
    RestPuller rp = new RestPuller(new UserStatusRecord(),mapReference.getApplicationContext());
    if(!SANDBOX)
      rp.start();
    // Get new records from the database
    ArrayList<UserStatusRecord> newRecords = new ArrayList<>();
    if(!SANDBOX)
    {
      for (RestRecordImpl rri : rp.getFetchedRecords())
        newRecords.add((UserStatusRecord) rri);
      Collections.sort(newRecords);
    }else
    {
      newRecords.add(new UserStatusRecord(-1, -1, true,
          SBOX_START, SBOX_WAYPS, SBOX_START, SBOX_END));
    }
    // Compare newly fetched routes with already existing ones
    // if there exists a new route we tell the MapActivity to display it
    if(activeRoutes.size() != newRecords.size())
    {
      for (UserStatusRecord usr : newRecords)
      {
        boolean exists = false;
        for (UserStatusRecord act : activeRoutes)
        {
          if(act.equals(usr))
          {
            exists = true;
            break;
          }
        }
        if(!exists)
        {
          ArrayList<LatLng> ll = usr.getWaypoints();
          requestDirection(usr.getStartPoint(), usr.getEndPoint(), ll);
          activeRoutes.add(usr);
        }
      }
    }

    // TODO update friend indicators on map
    for(UserStatusRecord usr : activeRoutes)
      mapReference.putFriendMarker(new FriendMarker(usr.getCurrentPosition()));

    cdt = new CountDownTimer(20000, 10000) {
      public void onTick(long millisUntilFinished) {
        System.out.println("FriendRouteHandler: timer heartbeat");
      }
      public void onFinish() {
        frhLoop();
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
      mapReference.putFriendDirection(direction);
  }

  @Override
  public void onDirectionFailure(Throwable t) {

  }
}

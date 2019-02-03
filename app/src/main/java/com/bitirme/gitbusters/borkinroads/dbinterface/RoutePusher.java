package com.bitirme.gitbusters.borkinroads.dbinterface;

import com.bitirme.gitbusters.borkinroads.routeutilities.RouteRecord;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RoutePusher extends Thread {

  public ArrayList<RouteRecord> toBePushed;

  public RoutePusher(ArrayList<RouteRecord> rr)
  {
    toBePushed = rr;
  }

  public RoutePusher(RouteRecord rr)
  {
    toBePushed = new ArrayList<>();
    toBePushed.add(rr);
  }

  /**
   * This segment is highly inspired by the snippets provided in the link below
   * https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117
   */
  @Override
  public void run() {
    HttpsURLConnection connPut = null;
    try {
      URL webServerUrl = new URL("https://shielded-cliffs-47552.herokuapp.com/routes.json");

      connPut = (HttpsURLConnection) webServerUrl.openConnection();

      connPut.setReadTimeout(10000 /* milliseconds */);
      connPut.setConnectTimeout(15000 /* milliseconds */);
      connPut.setDoOutput(true);

      connPut.setRequestMethod("POST");
      connPut.setRequestProperty("Content-type", "application/json");
      connPut.connect();
      OutputStreamWriter out = new OutputStreamWriter(connPut.getOutputStream());

      for(RouteRecord rr : toBePushed)
      {
        out.write(rr.getJSONRepresentation().toString());
        out.flush();
      }
      out.close();

      if(connPut.getResponseCode() == 200)
        System.out.println("Successfully posted new route(s).");
      else
        System.out.println(connPut.getResponseMessage());

      connPut.disconnect();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if(connPut!=null)
        connPut.disconnect();
    }
  }

}

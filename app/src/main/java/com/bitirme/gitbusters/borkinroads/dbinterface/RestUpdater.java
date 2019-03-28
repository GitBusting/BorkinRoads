package com.bitirme.gitbusters.borkinroads.dbinterface;

import android.content.Context;

import com.bitirme.gitbusters.borkinroads.data.RestRecord;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RestUpdater extends Thread {

  public ArrayList<RestRecordImpl> toBeUpdated;
  private String DB_URL;
  private Context context;

  /**
   * Creates a thread to bulk update all entries
   * @param rr all entries to be updated
   */
  public RestUpdater(ArrayList<RestRecordImpl> rr, Context context)
  {
    toBeUpdated = rr;
    DB_URL = rr.get(0).getURL();
    this.context = context;
  }

  /**
   * Creates a thread ready to update the specified entry
   * @param rr updated version of the record
   */
  public RestUpdater(RestRecordImpl rr, Context context)
  {
    if(rr.getEntryID() < 0)
      throw new AssertionError("Trying to update a route which has no ID.\n" +
          "This should only happen when the route has just been created and\n" +
          "hasn't been pushed to the database yet.");
    DB_URL = rr.getURL() + "/" + rr.getEntryID() + ".json";
    toBeUpdated = new ArrayList<>();
    toBeUpdated.add(rr);
    this.context = context;
  }

  /*
   * This segment is highly inspired by the snippets provided in the link below
   * https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117
   */
  @Override
  public void run() {
    HttpsURLConnection connPut = null;
    try {
      URL webServerUrl = new URL(DB_URL);

      connPut = (HttpsURLConnection) webServerUrl.openConnection();

      connPut.setReadTimeout(10000 /* milliseconds */);
      connPut.setConnectTimeout(15000 /* milliseconds */);
      connPut.setDoOutput(true);

      connPut.setRequestMethod("PUT");
      connPut.setRequestProperty("Content-type", "application/json");
      AuthenticationValidator authenticationValidator = new AuthenticationValidator(context);
      String token = authenticationValidator.getAuthenticationToken();
      connPut.addRequestProperty("Authorization", "Bearer " + token);
      connPut.connect();
      OutputStreamWriter out = new OutputStreamWriter(connPut.getOutputStream());

      for(RestRecord rr : toBeUpdated)
      {
        out.write(rr.getJSON().toString());
        out.flush();
      }
      out.close();

      if(connPut.getResponseCode() == 200)
        System.out.println("Successfully updated a route.");
      else
        System.out.println(connPut.getResponseCode() + " " + connPut.getResponseMessage());

      connPut.disconnect();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if(connPut!=null)
        connPut.disconnect();
    }
  }

}

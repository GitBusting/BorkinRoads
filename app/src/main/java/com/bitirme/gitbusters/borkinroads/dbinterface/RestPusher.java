package com.bitirme.gitbusters.borkinroads.dbinterface;

import com.bitirme.gitbusters.borkinroads.data.RestRecord;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RestPusher extends Thread {

  public ArrayList<RestRecordImpl> toBePushed;
  private String DB_URL;

  public RestPusher(ArrayList<RestRecordImpl> rr)
  {
    toBePushed = rr;
    DB_URL = rr.get(0).getURL();
  }

  public RestPusher(RestRecordImpl rr)
  {
    DB_URL = rr.getURL() + ".json";
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
      URL webServerUrl = new URL(DB_URL);

      connPut = (HttpsURLConnection) webServerUrl.openConnection();

      connPut.setReadTimeout(10000 /* milliseconds */);
      connPut.setConnectTimeout(15000 /* milliseconds */);
      connPut.setDoOutput(true);

      connPut.setRequestMethod("POST");
      connPut.setRequestProperty("Content-type", "application/json");
      connPut.connect();
      OutputStreamWriter out = new OutputStreamWriter(connPut.getOutputStream());

      for(RestRecord rr : toBePushed)
      {
        out.write(rr.getJSON().toString());
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

package com.bitirme.gitbusters.borkinroads.uiactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bitirme.gitbusters.borkinroads.R;
import com.bitirme.gitbusters.borkinroads.data.DoggoRecord;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;
import com.bitirme.gitbusters.borkinroads.data.UserRecord;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPuller;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestUpdater;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FriendListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  private static final boolean SANDBOX = false;

  private ScrollView   sv;
  private EditText     et;

  private ArrayList<UserRecord> allUsers;
  private ArrayList<UserRecord> friends;
  private ArrayList<UserRecord> notFriends;

  private ArrayList<Button> visibleButtonList;
  private LinearLayout ll;

  private String lastVisibleText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);

    Toolbar toolbar = findViewById(R.id.toolbar_friend);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);


    // Initialize UI elements
    sv = findViewById(R.id.scroll_view);
    et = findViewById(R.id.edit_text);
    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
          resetLayout();
        else
        {
          resetLayout();
          displayUsersOnLayout(friends, true);
        }
      }
    });

    et.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {}

      @Override
      public void afterTextChanged(Editable s) {
        resetLayout();
        String search = s.toString();
        ArrayList<UserRecord> fitting = searchUser(search);
        if(s.equals("") || s.length() < 1) {
          resetLayout();
        }
        else
          displayUsersOnLayout(fitting, false);
        lastVisibleText = s.toString();
      }
    });

    // Force lose focus when keyboard is hidden
    // https://stackoverflow.com/questions/11981740/how-to-lose-the
    // -focus-of-a-edittext-when-done-button-in-the-soft-keyboard-is-p
    et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if(actionId== EditorInfo.IME_ACTION_DONE){
          //Clear focus here from edittext
          et.clearFocus();
        }
        return false;
      }
    });

    lastVisibleText = null;
    visibleButtonList = new ArrayList<>();
    friends = new ArrayList<>();
    notFriends = new ArrayList<>();
    allUsers = new ArrayList<>();

    // Initialize user list
    // TODO potential buggy behavior
    RestPuller rp = new RestPuller(new UserRecord(),getApplicationContext());
    rp.start();
    try {
      rp.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (RestRecordImpl rri : rp.getFetchedRecords())
      allUsers.add((UserRecord) rri);

    // Initialize user's friend list
    UserRecord activeUser = UserRecord.activeUser;
    for (UserRecord ur : allUsers)
      if (activeUser.getFriendIds().contains(ur.getEntryID())) // is a friend
        friends.add(ur);

    // Fill the list "allUsersExceptFriends"

    for (int i = 0 ; i < allUsers.size() ; i++) {
      boolean foundFriend = false;
      for (int uid : activeUser.getFriendIds())
        if (allUsers.get(i).getEntryID() == uid)
          foundFriend = true;
      if(!foundFriend)
        notFriends.add(allUsers.get(i));
    }

    // Display user's friends on the scrollview layout
    ll = findViewById(R.id.linear_layout);
    displayUsersOnLayout(friends,true);
  }

  private ArrayList<UserRecord> searchUser(String s)
  {
    ArrayList<UserRecord> ret = new ArrayList<>();
    for(UserRecord potentialFriend : notFriends){
      String uName = potentialFriend.getName();
      if(uName.contains(s))
        ret.add(potentialFriend);
    }
    return ret;
  }


  /**
   * @param users List of users to display
   * @param friends Indicate whether listed users are the user's friends or not
   */
  private void displayUsersOnLayout(ArrayList<UserRecord> users, boolean friends)
  {
    for(UserRecord ur : users)
    {
      LinearLayout newLL = new LinearLayout(this);
      TextView tv = new TextView(this);
      String petText = "";
      for(DoggoRecord dr : ur.getPets())
        petText += dr.getName() + " ";
      tv.setGravity(Gravity.CENTER);
      tv.setText(ur.getName());
      newLL.addView(tv);
      final int userID = ur.getEntryID();
      if(friends)
      {
        Button rmButton = new Button(this);
        rmButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            removeFriend(userID);
          }
        });
        rmButton.setText("REMOVE");
        newLL.addView(rmButton);
        visibleButtonList.add(rmButton);
      }else
      {
        Button addButton = new Button(this);
        addButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            addFriend(userID);
          }
        });
        addButton.setText("ADD");
        newLL.addView(addButton);
        visibleButtonList.add(addButton);
      }
      ll.addView(newLL);
    }
  }

  private void resetLayout()
  {
    ViewGroup allEntries = ll;
    while(allEntries.getChildCount()>0)
      allEntries.removeViewAt(0);
    visibleButtonList.clear();
  }

  private final void removeFriend(int userID)
  {
    if(!UserRecord.activeUser.getFriendIds().remove((Object)userID))
    {
      throw new AssertionError("Tried to remove a non-existing friend, " +
          "are you really this desperate?");
    }
    RestUpdater ru = new RestUpdater(UserRecord.activeUser,this);
    ru.start();
    // So that we do not receive a concurrentmodificationexception
    int rm_idx = 0;
    for (int i = 0 ; i < friends.size() ; i++)
      if(friends.get(i).getEntryID() == userID)
        rm_idx = i;
    // Delete from friends & add the entry to notFriends
    UserRecord ur = friends.remove(rm_idx);
    notFriends.add(ur);
    resetLayout();
    displayUsersOnLayout(friends,true);
  }

  private final void addFriend(int userID)
  {
    UserRecord.activeUser.getFriendIds().add(userID);
    RestUpdater ru = new RestUpdater(UserRecord.activeUser, this);
    ru.start();
    Logger.getGlobal().log(Level.INFO,"userID to add: " + userID);
    UserRecord added = null;
    for (UserRecord ur : notFriends) {
      if (ur.getEntryID() == userID) {
        added = ur;
        friends.add(ur);
        break;
      }
    }
    if (added == null)
      throw new AssertionError("Tried to add a non-existing friend, " +
          "you really need some help.");
    if (!notFriends.remove(added))
      throw new AssertionError("Tried to add a friend that was already a friend, " +
          "please isolate yourself from society.");
    resetLayout();
    displayUsersOnLayout(searchUser(lastVisibleText), false);
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_addPet) {
      Intent i = new Intent(this, BreedDoggos.class);
      startActivity(i);

    } else if (id == R.id.nav_walk) {
      Intent i = new Intent(this, MapActivity.class);
      startActivity(i);
    } else if (id == R.id.nav_my_routes) {
      Intent i = new Intent(this, DisplayRoutesActivity.class);
      startActivity(i);
    } else if (id == R.id.nav_friend_list) {
      Intent i = new Intent(this, FriendListActivity.class);
      startActivity(i);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  // Quick aesthetics patch
  // https://stackoverflow.com/a/23005236/11131120
  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    EditText mEditText = findViewById(R.id.edit_text);
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      View v = getCurrentFocus();
      if (mEditText.isFocused()) {
        Rect outRect = new Rect();
        mEditText.getGlobalVisibleRect(outRect);
        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
          // Did not touch the edittext, but did it touch one of the buttons?
          for (Button b : visibleButtonList)
          {
            Rect buttonOutRect = new Rect();
            b.getGlobalVisibleRect(buttonOutRect);
            // If the user pressed the button, skip doing stuff
            if(buttonOutRect.contains((int)event.getRawX(), (int)event.getRawY()))
              return super.dispatchTouchEvent(event);
          }
          mEditText.clearFocus();
          //
          // Hide keyboard
          //
          InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
      }
    }
    return super.dispatchTouchEvent(event);
  }
}

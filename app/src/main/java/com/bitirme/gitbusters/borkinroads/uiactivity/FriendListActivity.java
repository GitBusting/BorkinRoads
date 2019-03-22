package com.bitirme.gitbusters.borkinroads.uiactivity;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

public class FriendListActivity extends AppCompatActivity {

  private static final boolean SANDBOX = false;

  private ScrollView   sv;
  private EditText     et;

  private ArrayList<UserRecord> allUsers;
  private ArrayList<UserRecord> friends;
  private ArrayList<UserRecord> notFriends;
  private LinearLayout ll;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);

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
        if(s.equals(""))
          resetLayout();
        else
          displayUsersOnLayout(fitting, false);
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
      for (int uid : activeUser.getFriendIds()) {
        if (allUsers.get(i).getEntryID() == uid)
          notFriends.add(allUsers.get(i));
      }
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
      tv.setText(ur.getName()+ ": " + petText);
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
      }
      ll.addView(newLL);
    }
  }

  private void resetLayout()
  {
    ViewGroup allEntries = ll;
    while(allEntries.getChildCount()>0)
      allEntries.removeViewAt(0);
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
    displayUsersOnLayout(notFriends, false);
  }

}

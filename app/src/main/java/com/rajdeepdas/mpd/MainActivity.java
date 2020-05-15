package com.rajdeepdas.mpd;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SearchView search;

    static DatabaseHelper myDbHelper;
    static boolean databaseOpened=false;


    SimpleCursorAdapter suggestionAdapter;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    boolean notificationenabled=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search =  (SearchView) findViewById(R.id.search_view);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                search.setIconified(false);
            }
        });


        myDbHelper = new DatabaseHelper(this);

        if(myDbHelper.checkDataBase())
        {
            openDatabase();

        }
        else
        {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }




        // setup SimpleCursorAdapter

        final String[] from = new String[] {"en_word"};
        final int[] to = new int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }

        };

        search.setSuggestionsAdapter(suggestionAdapter);

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {

                // Add clicked text to search box
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clicked_word =  cursor.getString(cursor.getColumnIndex("en_word"));
                search.setQuery(clicked_word,false);

                //search.setQuery("",false);

                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String text =  search.getQuery().toString();

                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m = p.matcher(text);



                if(m.matches())
                {
                    Cursor c = myDbHelper.getMeaning(text);

                    if(c.getCount()==0)
                    {
                        showAlertDialog();
                    }

                    else
                    {
                        //search.setQuery("",false);
                        search.clearFocus();
                        search.setFocusable(false);

                        Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("en_word",text);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                }
                else
                {
                    showAlertDialog();
                }



                return false;
            }


            @Override
            public boolean onQueryTextChange(final String s) {

                search.setIconifiedByDefault(false); //Give Suggestion list margins

                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m = p.matcher(s);


                if(m.matches())
                {
                    Cursor cursorSuggestion=myDbHelper.getSuggestions(s);
                    suggestionAdapter.changeCursor(cursorSuggestion);
                }
                return false;
            }

        });

        emptyHistory = (RelativeLayout)findViewById(R.id.empty_history);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        fetch_History();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                History j= historyList.get((viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,j.get_en_word()+"  is deleted",Toast.LENGTH_SHORT).show();
                remove1(j.get_en_word());
                historyList.remove((viewHolder.getAdapterPosition()));

                historyAdapter.notifyDataSetChanged();

            }
        }).attachToRecyclerView(recyclerView);


        //Start of Notification

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MyNotification","MyNotification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        final ImageButton btnNotify =(ImageButton)findViewById(R.id.btnNotify);

        SharedPreferences pref= getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("key",false);

        notificationenabled=pref.getBoolean("key",false);

        if(!notificationenabled) btnNotify.animate().alpha(1).setDuration(1);
        else  btnNotify.animate().alpha(0.5f).setDuration(1);

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                   if(notificationenabled)
                   {
                       editor.putBoolean("key",false);
                       editor.apply();
                       notificationenabled=false;
                       btnNotify.animate().alpha(1).setDuration(1000);
                       Toast.makeText(MainActivity.this,"Notification Diabled",Toast.LENGTH_SHORT).show();

                   }
                   else
                   {
                       editor.putBoolean("key",true);
                       editor.apply();
                       notificationenabled=true;
                       btnNotify.animate().alpha(0.3f).setDuration(1000);
                       Toast.makeText(MainActivity.this,"Notification Enabled", Toast.LENGTH_SHORT).show();
                   }

                  notification();

            }
        });




    }

    protected static void openDatabase()
    {
        try {
            myDbHelper.openDataBase();
            databaseOpened=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetch_History()
    {
        historyList = new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory(this,historyList);
        recyclerView.setAdapter(historyAdapter);

        History h;
        if(databaseOpened)
        {
            cursorHistory = myDbHelper.getHistory();
            if(cursorHistory.moveToFirst())
            {
                do{
                    h= new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                    historyList.add(h);
                }
                while (cursorHistory.moveToNext());
            }
            historyAdapter.notifyDataSetChanged();
            if(historyAdapter.getItemCount()==0)
            {
                emptyHistory.setVisibility(View.VISIBLE);
            }
            else
            {
                emptyHistory.setVisibility(View.GONE);
            }
        }
    }

    private void notification()
    {
        if(databaseOpened) {
            if (notificationenabled&&myDbHelper.isempty()) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(calendar.HOUR_OF_DAY,20);
                calendar.set(calendar.MINUTE, 06);

                Intent intent = new Intent(getApplicationContext(), Notification_reciever.class);

                Bundle bundle = new Bundle();
                bundle.putString("en_word", getrandomword());
                intent.putExtras(bundle);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                // Toast.makeText(MainActivity.this,"Notification Enabled", Toast.LENGTH_SHORT).show();
                // btnNotify.animate().alpha(0.5f).setDuration(1000);
            } else {
                Calendar calendar = Calendar.getInstance();

                calendar.set(calendar.HOUR_OF_DAY, 6);
                calendar.set(calendar.MINUTE, 00);

                Intent intent = new Intent(getApplicationContext(), Notification_reciever.class);

                Bundle bundle = new Bundle();
                bundle.putString("en_word", getrandomword());
                intent.putExtras(bundle);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                // Toast.makeText(MainActivity.this,"Notification Diabled",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void remove1(String en_word)
    {
        myDbHelper.deleteItem(en_word);
    }

    private String getrandomword()
    {
        String s=myDbHelper.getrandomword();
        return s;
    }

    private void showAlertDialog()
    {
        search.setQuery("",false);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle("Word Not Found");
        builder.setMessage("Please search again");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        search.clearFocus();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();

        if(id==R.id.action_addnewword)
        {
            Intent intent = new Intent(MainActivity.this,AddingNewWordActivity.class);
            startActivity(intent);
            return true;
        }

        if(id==R.id.action_history1)
        {
            Intent intent = new Intent(MainActivity.this,HistoryActivity1.class);
            startActivity(intent);
            return true;

        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_about)
        {
            LayoutInflater inflater=LayoutInflater.from(this);
            View view=inflater.inflate(R.layout.about_alert_dialog,null);

            Button btn =view.findViewById(R.id.janina);



            final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
            alertDialog.show();

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();

                }
            });

        }

        if (id == R.id.action_exit) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        fetch_History();
    }
}

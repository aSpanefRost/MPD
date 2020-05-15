package com.rajdeepdas.mpd;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by rajdeepdas on 30/04/20.
 */

public class HistoryActivity1 extends AppCompatActivity
{
    DatabaseHelper myDbHelper;

    ArrayList<History1> historyList1;
    RecyclerView recyclerView1;
    RecyclerView.LayoutManager layoutManager1;
    RecyclerView.Adapter historyAdapter1;

    RelativeLayout emptyHistory1;
    Cursor cursorHistory1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_history1);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("History");

      toolbar.setNavigationIcon(R.drawable.ic_arrow_back);


      myDbHelper = new DatabaseHelper(HistoryActivity1.this);
      try {
          myDbHelper.openDataBase();
      } catch (SQLException sqle) {
          throw sqle;
      }

      emptyHistory1 = (RelativeLayout)findViewById(R.id.empty_history1);
      recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view_history1);
      layoutManager1 = new LinearLayoutManager(HistoryActivity1.this);
      recyclerView1.setLayoutManager(layoutManager1);

      fetch_History1();


  }

  private void fetch_History1()
  {
      historyList1 = new ArrayList<>();
      historyAdapter1 = new RecyclerViewAdapterHistory1(getApplicationContext(),historyList1);
      recyclerView1.setAdapter(historyAdapter1);

           History1 h;


          cursorHistory1 = myDbHelper.getHistory1();
          if(cursorHistory1.moveToFirst())
          {
              do{
                  h= new History1(cursorHistory1.getString(cursorHistory1.getColumnIndex("word")));
                  historyList1.add(h);
              }
              while (cursorHistory1.moveToNext());
          }
          historyAdapter1.notifyDataSetChanged();
          if(historyAdapter1.getItemCount()==0)
          {
              emptyHistory1.setVisibility(View.VISIBLE);
          }
          else
          {
              emptyHistory1.setVisibility(View.GONE);
          }

  }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
  @Override
    protected void onResume()
  {
      super.onResume();
      fetch_History1();
  }




}

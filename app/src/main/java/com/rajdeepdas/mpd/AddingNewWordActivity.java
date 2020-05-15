package com.rajdeepdas.mpd;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by rajdeepdas on 01/05/20.
 */

public class AddingNewWordActivity extends AppCompatActivity {


    DatabaseHelper myDbHelper;
    Cursor c=null;
    EditText editText;
    String text;
    String text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addingnewword);

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add or Edit Word");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

          editText = (EditText) findViewById(R.id.edit_addnewword);

        Button btn =(Button)findViewById(R.id.btnaddnewword);

        myDbHelper = new DatabaseHelper(AddingNewWordActivity.this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 text = editText.getText().toString();
                 c= myDbHelper.getMeaning(text);


                if (c.moveToFirst())
                {
                    text1 = c.getString(c.getColumnIndex("en_definition"));
                    //Toast.makeText(AddingNewWordActivity.this, text, Toast.LENGTH_SHORT).show();
                    showAlertDialog();


                }


                else
                {

                    Intent intent = new Intent(AddingNewWordActivity.this,AddingNewWordsDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",text);
                    intent.putExtras(bundle);
                    startActivity(intent);

                   // Toast.makeText(AddingNewWordActivity.this,"Word does not Exists",Toast.LENGTH_SHORT).show();

                }


            }
        });




    }

    private void showAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddingNewWordActivity.this,R.style.MyDialogTheme);
        builder.setTitle("This Word Already Exists");
        builder.setMessage("Do you Want to Replace it?");
        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddingNewWordActivity.this,AddingNewWordsDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("check",true);
                        bundle.putString("en_word",text);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });

        String negativeText = "No";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}

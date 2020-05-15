package com.rajdeepdas.mpd;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rajdeepdas on 01/05/20.
 */

public class AddingNewWordsDetailsActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper;
    private EditText textinputdefinition;
    private EditText textinputsynonyms;
    private EditText textinputantonyms;
    private EditText textinputexample;
    Button btn;
    Boolean check;
    String enWord;
    Cursor c;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addingnewworddetails);

        Bundle bundle = getIntent().getExtras();
        check= bundle.getBoolean("check",false);
        enWord=bundle.getString("en_word");

        myDbHelper = new DatabaseHelper(AddingNewWordsDetailsActivity.this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        textinputdefinition=(EditText) findViewById(R.id.text_input_definition);
        textinputsynonyms=(EditText) findViewById(R.id.text_input_synonyms);
        textinputantonyms=(EditText) findViewById(R.id.text_input_antonyms);
        textinputexample=(EditText) findViewById(R.id.text_input_example);


        if(check)
        {
            c = myDbHelper.getMeaning(enWord);
            if (c.moveToFirst()) {

                textinputdefinition.setText(c.getString(c.getColumnIndex("en_definition")), TextView.BufferType.EDITABLE);
                textinputsynonyms.setText(c.getString(c.getColumnIndex("synonyms")), TextView.BufferType.EDITABLE);
                textinputantonyms.setText(c.getString(c.getColumnIndex("antonyms")), TextView.BufferType.EDITABLE);
                textinputexample.setText(c.getString(c.getColumnIndex("example")), TextView.BufferType.EDITABLE);
            }

        }

        btn =(Button)findViewById(R.id.btn_addworddetails);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                String definitionInput=textinputdefinition.getText().toString();
                String synonymsInput=textinputsynonyms.getText().toString();
                String antonymsInput=textinputantonyms.getText().toString();
                String exampleInput=textinputexample.getText().toString();
                if(check)
                {
                    myDbHelper.update(enWord,definitionInput,synonymsInput,antonymsInput,exampleInput);
                    Toast.makeText(AddingNewWordsDetailsActivity.this,"Word Updated",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddingNewWordsDetailsActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",enWord);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                {
                    myDbHelper.createnewword(enWord,definitionInput,synonymsInput,antonymsInput,exampleInput);
                    Toast.makeText(AddingNewWordsDetailsActivity.this,"Word Added to Dictionary",Toast.LENGTH_SHORT).show();
                    myDbHelper.update(enWord,definitionInput,synonymsInput,antonymsInput,exampleInput);
                    Toast.makeText(AddingNewWordsDetailsActivity.this,"Word Updated",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddingNewWordsDetailsActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",enWord);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

                //String input =definitionInput+"\n"+synonymsInput+"\n"+antonymsInput+"\n"+exampleInput;

                //Toast.makeText(AddingNewWordsDetailsActivity.this, input, Toast.LENGTH_SHORT).show();

            }
        });


    }



}

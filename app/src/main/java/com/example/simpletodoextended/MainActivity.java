package com.example.simpletodoextended;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static int EDIT_REQUEST_CODE =20; // numeric code to identify the edit activity

    //Keys used to pass data between the activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";


    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);

        setUpListViewListener();

    }
    public void onAddItem(View view){
        EditText etNewItem = findViewById(R.id.etNewItem);
        String newItem = etNewItem.getText().toString();
        itemsAdapter.add(newItem);
        etNewItem.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), newItem +" added to your to do list", Toast.LENGTH_SHORT).show();
    }
    private void setUpListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                Toast.makeText(getApplicationContext(),  "Item removed from the list", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create the new activity via the intent class
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                //Pass the data to be edited to the activity
                i.putExtra(ITEM_TEXT, items.get(position));
                i.putExtra(ITEM_POSITION, position);
                // Display the activity
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==EDIT_REQUEST_CODE){
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            int position = data.getExtras().getInt(ITEM_POSITION);
            items.set(position, updatedItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully",Toast.LENGTH_SHORT).show();

        }
    }

    private File getDataFile(){

        return new File(getFilesDir(), "todo.txt");
    }

    private void readItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("Main Activity", "Error reading file", e);
            items = new ArrayList<>();
        }
    }
    private void writeItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("Main Activity", "Error writing file", e);
        }
    }

}

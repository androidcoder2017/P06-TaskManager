package com.myapplicationdev.android.p06_taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    int actReqCode = 1;
    int editDeleteReqCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DBHelper dbh = new DBHelper(this);
        CharSequence reply = null;
        Intent intent = getIntent();
        int id = intent.getIntExtra("key", 0);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){
           if (reply.toString().equalsIgnoreCase("Completed")) {
               dbh.deleteTask(id);
           }

        }

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditDelete.class);
                Task selectedTask = new Task(dbh.getAllTasks().get(position).getId(), dbh.getAllTasks().get(position).getName(), dbh.getAllTasks().get(position).getDescription());
                i.putExtra("taskSelected", selectedTask);
                startActivityForResult(i, editDeleteReqCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        } else if(resultCode == RESULT_OK && requestCode == this.editDeleteReqCode) {

            DBHelper db = new DBHelper(this);
            tasks = db.getAllTasks();
            adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
            lv.setAdapter(adapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

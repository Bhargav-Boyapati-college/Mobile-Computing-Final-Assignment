package com.bhargav.to_do_list;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TaskDbHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(this);
        db = dbHelper.getWritableDatabase();

        ListView taskListView = findViewById(R.id.task_list_view);
        cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, null, null, null, null, null, null);
        adapter = new TaskAdapter();
        taskListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    private class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            cursor.moveToPosition(position);
            return cursor;
        }

        @Override
        public long getItemId(int position) {
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            TextView titleView = convertView.findViewById(android.R.id.text1);
            TextView descriptionView = convertView.findViewById(android.R.id.text2);
            Button doneButton = new Button(MainActivity.this);
            doneButton.setText("Done");

            ViewGroup layout = (ViewGroup) convertView;
            if (layout.getChildCount() == 2) {
                layout.addView(doneButton);
            }

            cursor.moveToPosition(position);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            long taskId = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));

            titleView.setText(title);
            descriptionView.setText(description);

            doneButton.setOnClickListener(v -> {
                // Delete the task from the database
                db.delete(TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID + " = ?",
                        new String[]{String.valueOf(taskId)});

                // Refresh the cursor and adapter
                cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, null, null, null, null, null, null);
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}

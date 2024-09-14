package com.pregrad.aprilandroid.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pregrad.aprilandroid.Data.DataBaseHelper;
import com.pregrad.aprilandroid.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        Button insert = findViewById(R.id.insert);
        Button read = findViewById(R.id.read);
        Button update = findViewById(R.id.update);
        Button delete = findViewById(R.id.delete);
        TextView data = findViewById(R.id.data);
        EditText id = findViewById(R.id.id);
        EditText name = findViewById(R.id.name);
        EditText surname = findViewById(R.id.surName);
        EditText marks = findViewById(R.id.marks);
        EditText grade = findViewById(R.id.grade);
        insert.setOnClickListener(v -> {
            if (id.getText().toString().isEmpty() || name.getText().toString().isEmpty() || surname.getText().toString().isEmpty() || marks.getText().toString().isEmpty() || grade.getText().toString().isEmpty()) {
                Toast.makeText(SecondActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                dataBaseHelper.insertData(Integer.parseInt(id.getText().toString()), name.getText().toString(), surname.getText().toString(), Integer.parseInt(marks.getText().toString()), grade.getText().toString());
                clearTextFields(id, name, surname, marks, grade);
            }
        });
        read.setOnClickListener(v -> {
            if (id.getText().toString().isEmpty()){
                Cursor cursor = dataBaseHelper.getAllData();
                if (cursor.getCount() == 0) {
                    Toast.makeText(SecondActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder buffer = new StringBuilder();
                    while (cursor.moveToNext()) {
                        buffer.append("ID: ").append(cursor.getInt(0)).append("\n");
                        buffer.append("Name: ").append(cursor.getString(1)).append("\n");
                        buffer.append("Surname: ").append(cursor.getString(2)).append("\n");
                        buffer.append("Marks: ").append(cursor.getInt(3)).append("\n");
                        buffer.append("Grade: ").append(cursor.getString(4)).append("\n\n");
                    }
                    data.setText(buffer.toString());
                }
            }else {
                Cursor cursor = dataBaseHelper.getDataByID(Integer.parseInt(id.getText().toString()));
                if (cursor.getCount() == 0) {
                    Toast.makeText(SecondActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder buffer = new StringBuilder();
                    while (cursor.moveToNext()) {
                        buffer.append("ID: ").append(cursor.getInt(0)).append("\n");
                        buffer.append("Name: ").append(cursor.getString(1)).append("\n");
                        buffer.append("Surname: ").append(cursor.getString(2)).append("\n");
                        buffer.append("Marks: ").append(cursor.getInt(3)).append("\n");
                        buffer.append("Grade: ").append(cursor.getString(4)).append("\n\n");
                    }
                    data.setText(buffer.toString());

                }
                clearTextFields(id, name, surname, marks, grade);
            }
        });
        update.setOnClickListener(v -> {
            if (id.getText().toString().isEmpty() || name.getText().toString().isEmpty() || surname.getText().toString().isEmpty() || marks.getText().toString().isEmpty() || grade.getText().toString().isEmpty()) {
                Toast.makeText(SecondActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                dataBaseHelper.updateData(Integer.parseInt(id.getText().toString()), name.getText().toString(), surname.getText().toString(), Integer.parseInt(marks.getText().toString()), grade.getText().toString());
                clearTextFields(id, name, surname, marks, grade);
            }
        });
        delete.setOnClickListener(v -> {
            if (id.getText().toString().isEmpty()) {
                dataBaseHelper.deleteAllData();
            }else {
                dataBaseHelper.deleteData(Integer.parseInt(id.getText().toString()));
                clearTextFields(id, name, surname, marks, grade);
            }
        });
    }

    private void clearTextFields(EditText id, EditText name, EditText surname, EditText marks, EditText grade) {
        id.setText("");
        name.setText("");
        surname.setText("");
        marks.setText("");
        grade.setText("");
    }
}
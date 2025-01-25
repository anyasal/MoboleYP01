package Model;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import Controller.DatabaseHelper;

public class AddNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        // Получаем ID пользователя из Intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Обработка нажатия на кнопку "Сохранить"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                if (!title.isEmpty() && !content.isEmpty()) {
                    dbHelper.addNote(userId, title, content);
                    finish(); // Закрываем активность после сохранения
                }
            }
        });
    }
}
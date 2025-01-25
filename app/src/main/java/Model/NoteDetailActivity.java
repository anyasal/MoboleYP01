package Model;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import Controller.DatabaseHelper;
import Controller.Note; // Убедитесь, что импортирован правильный класс

public class NoteDetailActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton, deleteButton;
    private DatabaseHelper dbHelper;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        dbHelper = new DatabaseHelper(this);

        // Получаем ID заметки из Intent
        noteId = getIntent().getIntExtra("NOTE_ID", -1);

        // Загружаем данные заметки
        Note note = dbHelper.getNoteById(noteId);
        if (note != null) {
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
        }

        // Обработка нажатия на кнопку "Сохранить"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                if (!title.isEmpty() && !content.isEmpty()) {
                    dbHelper.updateNote(noteId, title, content);
                    finish(); // Закрываем активность после обновления
                }
            }
        });

        // Обработка нажатия на кнопку "Удалить"
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteNote(noteId);
                finish(); // Закрываем активность после удаления
            }
        });

    }
}
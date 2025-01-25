package Model;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import Adapter.NotesAdapter;
import Controller.DatabaseHelper;
import Controller.Note;
import java.util.List;

public class NotesListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        dbHelper = new DatabaseHelper(this);
        notesListView = findViewById(R.id.notesListView);

        // Получаем ID пользователя (например, из Intent)
        int userId = getIntent().getIntExtra("USER_ID", -1);

        // Загружаем заметки для пользователя
        List<Note> notes = dbHelper.getNotesForUser(userId);

        // Создаем адаптер и устанавливаем его для ListView
        NotesAdapter adapter = new NotesAdapter(this,notes);
        notesListView.setAdapter(adapter);

        // Обработка клика по заметке
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(NotesListActivity.this, NoteDetailActivity.class);
                intent.putExtra("NOTE_ID", selectedNote.getId());
                startActivity(intent);
            }
        });

        // Кнопка для добавления новой заметки
        findViewById(R.id.addNoteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesListActivity.this, AddNoteActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }
}

package com.example.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import Controller.DatabaseHelper;
import Controller.LoginActivity;
import Controller.Note;
import Adapter.NotesAdapter;
import Model.AddNoteActivity;
import Model.NoteDetailActivity;
import Model.NotesClickListener;
import Model.NotesListActivity;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private NotesAdapter notesListAdapter;
    private DatabaseHelper databaseHelper;
    private List<Note> notes = new ArrayList<>();
    private Note selectedNote;
    private int currentUserId; // ID текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            currentUserId = intent.getIntExtra("USER_ID", -1);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
            currentUserId = sharedPreferences.getInt("user_id", -1);
        }

        if (currentUserId == -1) {
            // Если пользователь не авторизован, вернитесь на экран входа
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // Инициализация DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Получение ID текущего пользователя (например, из SharedPreferences или Intent)
        currentUserId = getCurrentUserId(); // Замените на реальный метод получения ID пользователя

        // Загрузка заметок для текущего пользователя
        notes = databaseHelper.getNotesForUser(currentUserId);

        // Настройка RecyclerView
        updateRecycler(notes);

        // Обработчик нажатия на кнопку добавления заметки
        fabAdd.setOnClickListener(v -> {
            Intent noteIntent = new Intent(MainActivity.this, NoteDetailActivity.class);
            startActivityForResult(noteIntent, 101);
        });
    }

    // Обновление RecyclerView
    private void updateRecycler(List<Note> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(notesListAdapter);
    }

    // Обработчик результатов активности
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Note note = (Note) data.getSerializableExtra("note");
            if (requestCode == 101) { // Добавление новой заметки
                databaseHelper.addNote(currentUserId, note.getTitle(), note.getContent());
            } else if (requestCode == 102) { // Редактирование существующей заметки
                databaseHelper.updateNote(note.getId(), note.getTitle(), note.getContent());
            }
            refreshNotesList();
        }
    }

    // Обновление списка заметок
    private void refreshNotesList() {
        notes.clear();
        notes.addAll(databaseHelper.getNotesForUser(currentUserId));
        notesListAdapter.notifyDataSetChanged();
    }

    // Обработчик кликов на заметках

    public void onClick(Note note) {
        Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
        intent.putExtra("NOTE_ID", note.getId());
        startActivityForResult(intent, 102);
    }



    // Показ всплывающего меню
    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    // Обработчик выбора пунктов всплывающего меню
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (selectedNote == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.pin) {
            togglePinStatus(selectedNote);
            return true;
        } else if (itemId == R.id.delete) {
            deleteNote(selectedNote);
            return true;
        }
        return false;
    }

    // Переключение статуса закрепления заметки
    private void togglePinStatus(Note note) {
        Toast.makeText(this, "Закрепление/открепление не реализовано", Toast.LENGTH_SHORT).show();
    }

    // Удаление заметки
    private void deleteNote(Note note) {
        databaseHelper.deleteNote(note.getId());
        notes.remove(note);
        notesListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
    }

    // Получение ID текущего пользователя
    private int getCurrentUserId() {

        return 1;
    }
}
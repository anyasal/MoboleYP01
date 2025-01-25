package Model;

import android.view.View;

import Controller.Note;

public interface NotesClickListener {
    void onClick(Note note); // Обработчик клика на заметке
    void onLongClick(Note note, View view); // Обработчик долгого нажатия на заметке
}
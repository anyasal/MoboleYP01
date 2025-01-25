package Controller;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Новая таблица для заметок
    public static final String TABLE_NOTES = "Notes";
    public static final String COLUMN_NOTE_ID = "_note_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    // SQL-запрос для создания таблицы Notes
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + "(" +
                    COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Сохраняем контекст для использования в Toast
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        }
    }
    public int getUserId(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID}, // Убедитесь, что столбец COLUMN_ID существует
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        int userId = -1; // Значение по умолчанию, если пользователь не найден
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) { // Проверяем, что столбец существует
                userId = cursor.getInt(columnIndex);
            } else {
                // Обработка ошибки: столбец не найден
                throw new IllegalArgumentException("Столбец " + COLUMN_ID + " не найден в таблице " + TABLE_USERS);
            }
        }
        cursor.close();
        return userId;
    }

    public boolean checkUserExists(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // Методы для работы с заметками

    // Метод для получения заметки по её ID
    public Note getNoteById(int noteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{
                        COLUMN_NOTE_ID,
                        COLUMN_TITLE,
                        COLUMN_CONTENT
                },
                COLUMN_NOTE_ID + "=?",
                new String[]{String.valueOf(noteId)},
                null, null, null
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note();
            int columnIndexNoteId = cursor.getColumnIndex(COLUMN_NOTE_ID);
            int columnIndexTitle = cursor.getColumnIndex(COLUMN_TITLE);
            int columnIndexContent = cursor.getColumnIndex(COLUMN_CONTENT);

            if (columnIndexNoteId >= 0 && columnIndexTitle >= 0 && columnIndexContent >= 0) {
                note.setId(cursor.getInt(columnIndexNoteId));
                note.setTitle(cursor.getString(columnIndexTitle));
                note.setContent(cursor.getString(columnIndexContent));
            }
        }
        cursor.close();
        return note;
    }
    public long addNote(int userId, String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        return db.insert(TABLE_NOTES, null, values);
    }

    public List<Note> getNotesForUser(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{
                        COLUMN_NOTE_ID,
                        COLUMN_TITLE,
                        COLUMN_CONTENT
                },
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );


        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = new Note();
            int columnIndexNoteId = cursor.getColumnIndex(COLUMN_NOTE_ID);
            int columnIndexTitle = cursor.getColumnIndex(COLUMN_TITLE);
            int columnIndexContent = cursor.getColumnIndex(COLUMN_CONTENT);

            if (columnIndexNoteId >= 0 && columnIndexTitle >= 0 && columnIndexContent >= 0) {
                note.setId(cursor.getInt(columnIndexNoteId));
                note.setTitle(cursor.getString(columnIndexTitle));
                note.setContent(cursor.getString(columnIndexContent));
                notes.add(note);
            }
        }
        cursor.close();
        return notes;
    }

    public void updateNote(int noteId, String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        db.update(TABLE_NOTES, values, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
    }

    // Новые методы

    // Метод для отображения сообщения об ошибке
    private void showErrorMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    // Метод для проверки аутентификации пользователя
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password}
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            return true; // Пользователь найден
        } else {
            cursor.close();
            showErrorMessage("Неверный логин или пароль"); // Сообщение об ошибке
            return false; // Пользователь не найден
        }
    }

    // Метод для проверки корректности ввода данных
    public boolean isValidInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showErrorMessage("Логин не может быть пустым");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            showErrorMessage("Пароль не может быть пустым");
            return false;
        }
        if (password.length() < 6) {
            showErrorMessage("Пароль должен содержать не менее 6 символов");
            return false;
        }
        return true; // Данные корректны
    }
}
package Controller;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;


public class LoginActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private TextView errorMessage;
    private Button buttonLogin, buttonRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        errorMessage = findViewById(R.id.error_message);
        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_register);
        dbHelper = new DatabaseHelper(this);

        // Обработка нажатия на кнопку входа
        buttonLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                if (dbHelper.authenticateUser(username, password)) {
                    int userId = dbHelper.getUserId(username);
                    saveUserId(userId);
                    startMainActivity(userId);
                } else {
                    showErrorMessage("Неверный логин или пароль");
                }
            }
        });

        // Обработка нажатия на кнопку регистрации
        buttonRegister.setOnClickListener(v -> {
            // Переход на страницу регистрации
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Проверка ввода данных
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showErrorMessage("Имя пользователя не может быть пустым");
            return false;
        }
        if (password.isEmpty()) {
            showErrorMessage("Пароль не может быть пустым");
            return false;
        }
        if (password.length() < 6) {
            showErrorMessage("Пароль должен содержать не менее 6 символов");
            return false;
        }
        return true;
    }

    // Отображение сообщения об ошибке
    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    // Сохранение ID пользователя в SharedPreferences
    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }

    // Переход на MainActivity
    private void startMainActivity(int userId) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_ID", userId); // Передаем ID пользователя
        startActivity(intent);
        finish();  // Закрыть LoginActivity
    }
}
package Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button buttonRegister;
    private TextView errorMessage;

    private DatabaseHelper dbHelper; // Для работы с базой данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация элементов UI
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        buttonRegister = findViewById(R.id.button_register); // Используйте поле класса
        errorMessage = findViewById(R.id.error_message);

        // Инициализация DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Обработчик нажатия кнопки регистрации
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if (isValidInput(username, password, confirmPassword)) {
                    long userId = dbHelper.addUser(username, password);
                    if (userId != -1) {
                        // Успешная регистрация
                        Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        finish(); // Закрываем экран регистрации
                    } else {
                        showErrorMessage("Пользователь с таким логином уже существует.");
                    }
                }
            }
        });
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, RegisterActivity.class);
        return intent;
    }

    // Проверяем введённые данные
    private boolean isValidInput(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            showErrorMessage("Пожалуйста, введите имя пользователя.");
            return false;
        }
        if (password.isEmpty()) {
            showErrorMessage("Пожалуйста, введите пароль.");
            return false;
        }
        if (password.length() < 6) {
            showErrorMessage("Пароль должен содержать не менее 6 символов.");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            showErrorMessage("Пожалуйста, подтвердите пароль.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Пароли не совпадают.");
            return false;
        }
        return true;
    }

    // Отображаем сообщение об ошибке
    private void showErrorMessage(String message) {
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(message);
    }
}
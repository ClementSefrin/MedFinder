package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import iut.dam.sae_dam.MedFind;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;


public class LoginActivity extends AppCompatActivity {
    private boolean dataLoaded = false;
    EditText mailET, passwordET;
    TextView errorMailTV, errorPasswordTV, errorDataTV;
    Button signUpBTN, forgotPasswordBTN, logInBTN;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    ImageButton passwordVisibilityBTN;
    private static final long SPLASH_SCREEN_DELAY = 2000;
    private Handler handler = new Handler();
    private int step = 0;
    private Runnable CheckDataLoadedRunnable = new Runnable() {
        @Override
        public void run() {
            if (!DataHandling.isDataLoaded()) {
                errorDataTV.setText(getString(R.string.waitingData) + (step % 2 == 0 ? "." : ""));
                step++;
                handler.postDelayed(this, 500);
            } else {
                errorDataTV.setVisibility(View.INVISIBLE);
                dataLoaded = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            Thread.sleep(SPLASH_SCREEN_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();
        handler.post(CheckDataLoadedRunnable);

        forgotPasswordBTN.setOnClickListener(v -> {
            if (dataLoaded) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.errorDataLoaded), Toast.LENGTH_SHORT).show();
            }
        });

        signUpBTN.setOnClickListener(v -> {
            if (dataLoaded) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.errorDataLoaded), Toast.LENGTH_SHORT).show();
            }
        });

        passwordVisibilityBTN.setOnClickListener(v -> {
            changePasswordVisibility();
        });

        logInBTN.setOnClickListener(v -> {
            logIn();
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    passwordET.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    passwordET.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });
    }

    private void logIn() {
        errors = getErrors();
        if (errors.isEmpty()) {
            if (dataLoaded) {
                new LogInTask().execute();
            } else {
                Toast.makeText(this, getString(R.string.errorDataLoaded), Toast.LENGTH_SHORT).show();
            }
        } else {
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        }
    }

    private class LogInTask extends AsyncTask<Void, Void, Void> {
        boolean exists = false;
        boolean passwordCorrect = false;
        private int userId, admin, city, secretQuestionIdx;
        private String password, secretAnswer;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "SELECT * FROM user WHERE Email = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mailET.getText().toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    exists = true;
                    if (resultSet.getString("Password").equals(passwordET.getText().toString())) {
                        passwordCorrect = true;
                        userId = resultSet.getInt("Id");
                        password = resultSet.getString("Password");
                        admin = resultSet.getInt("Administrator");
                        city = resultSet.getInt("City");
                        secretQuestionIdx = resultSet.getInt("QuestionSecrete");
                        secretAnswer = resultSet.getString("ReponseSecrete");
                    }
                }

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exists && passwordCorrect) {
                ErrorManager.updateBorder(getApplicationContext(), errors, errorMessagesViews);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("password", password);
                intent.putExtra("admin", admin == 0);
                intent.putExtra("city", city);
                intent.putExtra("secretQuestion", secretQuestionIdx);
                intent.putExtra("secretAnswer", secretAnswer);
                DataHandling.getIntentData(userId, password, admin == 0, city);
                startActivity(intent);

            } else {
                errors.put(mailET, Errors.EMPTY);
                errors.put(passwordET, Errors.INVALID_MAIL_PASSWORD);
                ErrorManager.updateBorder(getApplicationContext(), errors, errorMessagesViews);
            }
        }
    }

    private void changePasswordVisibility() {
        int passwordSelectionStart = passwordET.getSelectionStart();
        int passwordSelectionEnd = passwordET.getSelectionEnd();

        if (passwordET.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_hide_password);
        } else {
            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_show_password);
        }
        passwordET.setSelection(passwordSelectionStart, passwordSelectionEnd);
    }

    private void getViews() {
        mailET = findViewById(R.id.login_mailET);
        passwordET = findViewById(R.id.login_passwordET);
        signUpBTN = findViewById(R.id.login_noAccountBTN);
        forgotPasswordBTN = findViewById(R.id.login_forgotPasswordBTN);
        logInBTN = findViewById(R.id.login_logInBTN);
        passwordVisibilityBTN = findViewById(R.id.login_passwordVisibilityBTN);

        errorMailTV = findViewById(R.id.login_errorMailTV);
        errorPasswordTV = findViewById(R.id.login_errorPasswordTV);

        errorDataTV = findViewById(R.id.login_errorDataTV);

        errorMessagesViews.put(mailET, errorMailTV);
        errorMessagesViews.put(passwordET, errorPasswordTV);

        for (View view : errorMessagesViews.keySet()) {
            errorMessagesViews.get(view).setVisibility(View.GONE);
        }
    }

    private HashMap<View, Errors> getErrors() {
        HashMap<View, Errors> currentErrors = new HashMap<>();
        if (!ErrorManager.checkEmail(mailET.getText().toString())) {
            currentErrors.put(mailET, Errors.INVALID_MAIL_FORMAT);
            currentErrors.put(passwordET, Errors.EMPTY);
        }
        return currentErrors;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(CheckDataLoadedRunnable);
    }
}

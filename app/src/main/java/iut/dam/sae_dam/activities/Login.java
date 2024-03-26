package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;


public class Login extends AppCompatActivity {

    EditText mailET, passwordET;
    Button signUpBTN, forgotPasswordBTN, logInBTN;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    ImageButton passwordVisibilityBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();

        forgotPasswordBTN.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });

        signUpBTN.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, CreateAccount.class);
            startActivity(intent);
        });

        passwordVisibilityBTN.setOnClickListener(v -> {
            changePasswordVisibility();
        });

        logInBTN.setOnClickListener(v -> {
            logIn();
        });
    }

    private void logIn() {
        errors = getErrors();
        if (errors.isEmpty()) {
            new LogInTask().execute();
        } else {
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        }
    }

    private class LogInTask extends AsyncTask<Void, Void, Void> {
        boolean exists = false;
        boolean passwordCorrect = false;

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
                    }
                }

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("Database Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exists && passwordCorrect) {
                ErrorManager.updateBorder(getApplicationContext(), errors, errorMessagesViews);
                Intent intent = new Intent(Login.this, MainActivity.class);
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

        errorMessagesViews.put(mailET, findViewById(R.id.login_errorMailTV));
        errorMessagesViews.put(passwordET, findViewById(R.id.login_errorPasswordTV));

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
}

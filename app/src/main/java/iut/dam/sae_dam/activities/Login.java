package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;


public class Login extends AppCompatActivity {

    EditText mailET, passwordET;

    Button signUpBTN, forgotPasswordBTN, logInBTN;

    private List<View> invalidFields;
    private List<EditText> editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        invalidFields = new ArrayList<>();
        editTexts = new ArrayList<>();

        mailET = findViewById(R.id.login_mailET);
        passwordET = findViewById(R.id.login_passwordET);
        signUpBTN = findViewById(R.id.login_noAccountBTN);
        forgotPasswordBTN = findViewById(R.id.login_forgotPasswordBTN);
        logInBTN = findViewById(R.id.login_logInBTN);


        ViewGroup layout = findViewById(R.id.loginRL);
        editTexts = getAllEditTexts(layout);

        forgotPasswordBTN.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });

        signUpBTN.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, CreateAccount.class);
            startActivity(intent);
        });

        logInBTN.setOnClickListener(v -> {
            logIn();
        });
    }

    private void logIn() {
        ViewGroup layout = findViewById(R.id.createAccountRL);
        invalidFields.clear();
        invalidFields = getInvalidFields();
        if (invalidFields.isEmpty()) {
            new LogInTask().execute();
        } else {
            Toast.makeText(Login.this, "L'email ou le mot de passe est incorrect!", Toast.LENGTH_SHORT).show();
            updateBorder();
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
            if (exists) {
                invalidFields.clear();
                if (passwordCorrect) {
                    updateBorder();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    invalidFields.add(passwordET);
                    updateBorder();
                    Toast.makeText(Login.this, "L'email ou le mot de passe est incorrect!", Toast.LENGTH_SHORT).show();
                }
            } else {
                invalidFields.clear();
                invalidFields.add(mailET);
                invalidFields.add(passwordET);
                updateBorder();
                Toast.makeText(Login.this, "L'email ou le mot de passe est incorrect!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private List<EditText> getAllEditTexts(ViewGroup viewGroup) {
        List<EditText> editTexts = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof EditText) {
                editTexts.add((EditText) child);
            } else if (child instanceof ViewGroup) {
                editTexts.addAll(getAllEditTexts((ViewGroup) child));
            }
        }
        return editTexts;
    }

    private void updateBorder() {
        for (EditText field : editTexts) {
            if (invalidFields.contains(field)) {
                field.setBackgroundResource(R.drawable.invalid_edittext_border);
            } else {
                field.setBackgroundResource(R.drawable.valid_edittext_border);
            }
        }
    }

    private List<View> getInvalidFields() {
        List<View> invalidFields = new ArrayList<>();
        if (!checkEmail(mailET.getText().toString())) {
            invalidFields.add(mailET);
        }
        if (passwordET.getText().toString().isEmpty()) {
            invalidFields.add(passwordET);
        }
        return invalidFields;
    }
}

package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.villes.VilleAdapter;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText firstNameET, lastNameET, mailET, passwordET, passwordVerifyEt, secretAnswerET;
    private AutoCompleteTextView cityET;
    private TextView errorFirstNameTV, errorLastNameTV, errorMailTV, errorCityTV, errorPasswordTV, errorPasswordVerifyTV, errorSecretQuestion, errorSecretAnswerTV;

    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    private Spinner secretQuestionSP;
    private Button signUpBTN, logInBTN;
    ImageButton passwordVisibilityBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();

        logInBTN.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        passwordVisibilityBTN.setOnClickListener(v -> {
            changePasswordVisibility();
        });

        signUpBTN.setOnClickListener(v -> {
            createAccount();
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

        passwordVerifyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    passwordVerifyEt.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    passwordVerifyEt.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });
    }

    private void createAccount() {
        errors = getErrors();
        if (errors.isEmpty()) {
            new CheckAccountTask().execute();
        } else {
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        }
    }

    private class CheckAccountTask extends AsyncTask<Void, Void, Void> {
        private boolean exists;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "SELECT * FROM user WHERE Email = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mailET.getText().toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    this.exists = true;
                } else {
                    this.exists = false;
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
            if (exists) {
                errors.put(mailET, Errors.ACCOUNT_ALREADY_EXISTS);
                ErrorManager.updateBorder(getApplicationContext(), errors, errorMessagesViews);
            } else {
                ErrorManager.updateBorder(getApplicationContext(), errors, errorMessagesViews);
                new CreateAccountTask().execute();
            }
        }
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Void> {
        private int userId, admin;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "INSERT INTO user (FirstName, LastName, Email, Password, Administrator, City, QuestionSecrete, ReponseSecrete) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstNameET.getText().toString());
                preparedStatement.setString(2, lastNameET.getText().toString());
                preparedStatement.setString(3, mailET.getText().toString());
                preparedStatement.setString(4, passwordET.getText().toString());
                preparedStatement.setInt(5, 1);
                preparedStatement.setInt(6, Integer.parseInt(cityET.getText().toString().split(" - ")[1].trim()));

                int selectedPosition = secretQuestionSP.getSelectedItemPosition();
                String[] secretQuestionsArray = getResources().getStringArray(R.array.questionsSecretes);
                if (selectedPosition >= 0 && selectedPosition < secretQuestionsArray.length) {
                    int questionResourceId = getResources().getIdentifier(
                            secretQuestionsArray[selectedPosition], "string", getPackageName());
                }
                preparedStatement.setInt(7, selectedPosition);
                preparedStatement.setString(8, secretAnswerET.getText().toString());
                preparedStatement.executeUpdate();

                query = "SELECT Id, Administrator FROM user WHERE Email = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mailET.getText().toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                userId = resultSet.getInt("Id");
                admin = resultSet.getInt("Administrator");

                preparedStatement.close();
                Log.e("Database Connection", "Compte créé avec succès!");

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
            Toast.makeText(CreateAccountActivity.this, getString(R.string.accountCreatedMessage), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("password", passwordET.getText().toString());
            intent.putExtra("admin", admin == 0);
            intent.putExtra("city", cityET.getText().toString());
            intent.putExtra("secretQuestion", secretQuestionSP.getSelectedItemPosition());
            intent.putExtra("secretAnswer", secretAnswerET.getText().toString());
            DataHandling.getIntentData(userId, passwordET.getText().toString(), admin == 0, Integer.parseInt(cityET.getText().toString().split(" - ")[1].trim()));
            startActivity(intent);
        }
    }

    private void changePasswordVisibility() {
        int passwordSelectionStart = passwordET.getSelectionStart();
        int passwordSelectionEnd = passwordET.getSelectionEnd();

        int passwordVerifySelectionStart = passwordVerifyEt.getSelectionStart();
        int passwordVerifySelectionEnd = passwordVerifyEt.getSelectionEnd();

        if (passwordET.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVerifyEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_hide_password);
        } else {
            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVerifyEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_show_password);
        }

        passwordET.setSelection(passwordSelectionStart, passwordSelectionEnd);
        passwordVerifyEt.setSelection(passwordVerifySelectionStart, passwordVerifySelectionEnd);
    }

    private void getViews() {
        firstNameET = findViewById(R.id.createAccount_firstNameET);
        lastNameET = findViewById(R.id.createAccount_lastNameET);
        mailET = findViewById(R.id.createAccount_mailET);
        cityET = findViewById(R.id.createAccount_cityET);
        passwordET = findViewById(R.id.createAccount_passwordET);
        passwordVerifyEt = findViewById(R.id.createAccount_verifyPasswordET);
        secretQuestionSP = findViewById(R.id.createAccount_secretQuestionSP);
        secretAnswerET = findViewById(R.id.createAccount_secretAnswerET);
        signUpBTN = findViewById(R.id.createAccount_signUpBTN);
        logInBTN = findViewById(R.id.createAccount_alreadySignedUpBTN);
        passwordVisibilityBTN = findViewById(R.id.createAccount_passwordVisibilityBTN);

        errorFirstNameTV = findViewById(R.id.createAccount_errorFirstNameTV);
        errorLastNameTV = findViewById(R.id.createAccount_errorLastNameTV);
        errorMailTV = findViewById(R.id.createAccount_errorMailTV);
        errorCityTV = findViewById(R.id.createAccount_errorCityTV);
        errorPasswordTV = findViewById(R.id.createAccount_errorPasswordTV);
        errorPasswordVerifyTV = findViewById(R.id.createAccount_errorVerifyPasswordTV);
        errorSecretQuestion = findViewById(R.id.createAccount_errorSecretQuestionTV);
        errorSecretAnswerTV = findViewById(R.id.createAccount_errorSecretAnswerTV);

        errorMessagesViews.put(firstNameET, errorFirstNameTV);
        errorMessagesViews.put(lastNameET, errorLastNameTV);
        errorMessagesViews.put(mailET, errorMailTV);
        errorMessagesViews.put(cityET, errorCityTV);
        errorMessagesViews.put(passwordET, errorPasswordTV);
        errorMessagesViews.put(passwordVerifyEt, errorPasswordVerifyTV);
        errorMessagesViews.put(secretQuestionSP, errorSecretQuestion);
        errorMessagesViews.put(secretAnswerET, errorSecretAnswerTV);

        for (View view : errorMessagesViews.keySet()) {
            errorMessagesViews.get(view).setVisibility(View.GONE);
        }

        VilleAdapter cityAdapter = new VilleAdapter(this, DataHandling.getVilles());
        cityET.setAdapter(cityAdapter);
    }

    private HashMap<View, Errors> getErrors() {
        HashMap<View, Errors> errors = new HashMap<>();
        if (firstNameET.getText().toString().isEmpty()) {
            errors.put(firstNameET, Errors.EMPTY_FIELD);
        }
        if (lastNameET.getText().toString().isEmpty()) {
            errors.put(lastNameET, Errors.EMPTY_FIELD);
        }
        if (cityET.getText().toString().isEmpty()) {
            errors.put(cityET, Errors.EMPTY_FIELD);
        } else if (DataHandling.getVilles().stream().noneMatch(city -> city.getName().equalsIgnoreCase(cityET.getText().toString().split(" - ")[0].trim()))) {
            errors.put(cityET, Errors.UNKNOWN_CITY);
        }
        if (!ErrorManager.checkPassword(passwordET.getText().toString())) {
            errors.put(passwordET, Errors.INVALID_PASSWORD_FORMAT);
            errors.put(passwordVerifyEt, Errors.EMPTY);
        } else if (!ErrorManager.checkPasswordVerify(passwordET.getText().toString(), passwordVerifyEt.getText().toString())) {
            errors.put(passwordVerifyEt, Errors.INVALID_PASSWORD_CONFIRMATION);
        }
        if (!ErrorManager.checkEmail(mailET.getText().toString())) {
            errors.put(mailET, Errors.INVALID_MAIL_FORMAT);
        }
        if (secretQuestionSP.getSelectedItemPosition() == 0) {
            errors.put(secretQuestionSP, Errors.EMPTY_FIELD);
        }
        if (secretAnswerET.getText().toString().isEmpty()) {
            errors.put(secretAnswerET, Errors.EMPTY_FIELD);
        }
        return errors;
    }

}
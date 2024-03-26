package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.errors.ErrorMessages;
import iut.dam.sae_dam.errors.Errors;

public class CreateAccount extends AppCompatActivity {
    private EditText firstNameET, lastNameET, mailET, cityET, passwordET, passwordVerifyEt, secretAnswerET;
    private TextView errorFirstNameTV, errorLastNameTV, errorMailTV, errorCityTV, errorPasswordTV, errorPasswordVerifyTV, errorSecretQuestion, errorSecretAnswerTV;

    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;

    private Spinner secretQuestionSP;
    private Button signUpBTN, Btn_AlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        errors = new HashMap<>();
        getViews();

        Btn_AlreadyAccount = findViewById(R.id.createAccount_alreadySignedUpBTN);
        Btn_AlreadyAccount.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, Login.class);
            startActivity(intent);
        });

        signUpBTN = findViewById(R.id.createAccount_signUpBTN);
        signUpBTN.setOnClickListener(v -> {
            createAccount();
        });
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

        errorFirstNameTV = findViewById(R.id.createAccount_errorFirstNameTV);
        errorLastNameTV = findViewById(R.id.createAccount_errorLastNameTV);
        errorMailTV = findViewById(R.id.createAccount_errorMailTV);
        errorCityTV = findViewById(R.id.createAccount_errorCityTV);
        errorPasswordTV = findViewById(R.id.createAccount_errorPasswordTV);
        errorPasswordVerifyTV = findViewById(R.id.createAccount_errorVerifyPasswordTV);
        errorSecretQuestion = findViewById(R.id.createAccount_errorSecretQuestionTV);
        errorSecretAnswerTV = findViewById(R.id.createAccount_errorSecretAnswerTV);

        errorMessagesViews = new HashMap<>();
        errorMessagesViews.put(firstNameET, errorFirstNameTV);
        errorMessagesViews.put(lastNameET, errorLastNameTV);
        errorMessagesViews.put(mailET, errorMailTV);
        errorMessagesViews.put(cityET, errorCityTV);
        errorMessagesViews.put(passwordET, errorPasswordTV);
        errorMessagesViews.put(passwordVerifyEt, errorPasswordVerifyTV);
        errorMessagesViews.put(secretQuestionSP, errorSecretQuestion);
        errorMessagesViews.put(secretAnswerET, errorSecretAnswerTV);
    }

    private void createAccount() {

        ViewGroup layout = findViewById(R.id.createAccountRL);
        errors = getErrors();
        if (errors.isEmpty()) {
            new CheckAccountTask().execute();
        } else {
            updateBorder();
        }
    }

    private void updateBorder() {
        for (View field : errors.keySet()) {
            field.setBackgroundResource(R.drawable.invalid_edittext_border);
            errorMessagesViews.get(field).setText(ErrorMessages.getErrorMessage(this, errors.get(field)));
            errorMessagesViews.get(field).setVisibility(View.VISIBLE);
        }
    }


    private HashMap<View, Errors> getErrors() {
        HashMap<View, Errors> errors = new HashMap<>();
        if (firstNameET.getText().toString().isEmpty()) {
            errors.put(firstNameET, Errors.EMPTY);
        }
        if (lastNameET.getText().toString().isEmpty()) {
            errors.put(lastNameET, Errors.EMPTY);
        }
        if (cityET.getText().toString().isEmpty()) {
            errors.put(cityET, Errors.EMPTY);
        }
        switch (checkPassword(passwordET.getText().toString(), passwordVerifyEt.getText().toString())) {
            case -1:
                errors.put(passwordET, Errors.EMPTY);
                errors.put(passwordVerifyEt, Errors.EMPTY);
                break;
            case 1:
                errors.put(passwordET, Errors.INVALID_PASSWORD_CONFIRMATION);
                break;
            default:
                break;
        }
        if (!checkEmail(mailET.getText().toString())) {
            errors.put(mailET, Errors.INVALID_MAIL_FORMAT);
        }
        if (secretQuestionSP.getSelectedItemPosition() == 0) {
            errors.put(secretQuestionSP, Errors.EMPTY);
        }
        if (secretAnswerET.getText().toString().isEmpty()) {
            errors.put(secretAnswerET, Errors.EMPTY);
        }
        return errors;
    }

    private boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * Renvoie -1 si le mot de passe est vide, 1 si les mots de passe ne correspondent pas, 0 sinon
     */
    private int checkPassword(String mdp, String mdpVerify) {
        return mdp.isEmpty() ? -1 : !mdpVerify.equals(mdp) ? 1 : 0;
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
                    Log.e("Database Connection", "Un compte existe déjà avec cet email!");
                    this.exists = true;
                } else {
                    this.exists = false;
                    Log.e("Database Connection", "Aucun compte trouvé avec cet email!");
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
                errors.put(mailET, Errors.ACCOUNT_ALREADY_EXISTS);
                updateBorder();
            } else {
                updateBorder();
                new CreateAccountTask().execute();
            }
        }
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "INSERT INTO user (FirstName, SecondName, Email, Password, Administrator, City, QuestionSecrete, ReponseSecrete) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstNameET.getText().toString());
                preparedStatement.setString(2, lastNameET.getText().toString());
                preparedStatement.setString(3, mailET.getText().toString());
                preparedStatement.setString(4, passwordET.getText().toString());
                preparedStatement.setInt(5, 1);
                preparedStatement.setString(6, cityET.getText().toString());
                //TODO : mettre dans la base de données la question dans une langue précise
                preparedStatement.setString(7, secretQuestionSP.getSelectedItem().toString());
                preparedStatement.setString(8, secretAnswerET.getText().toString());
                preparedStatement.executeUpdate();
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
            Toast.makeText(CreateAccount.this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateAccount.this, MainActivity.class);
            //TODO: passer les infos de connexion
            startActivity(intent);
        }
    }
}
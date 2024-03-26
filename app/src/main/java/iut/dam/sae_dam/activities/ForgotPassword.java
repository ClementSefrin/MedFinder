package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;

public class ForgotPassword extends AppCompatActivity {

    private EditText mailET, newPasswordET, verifyNewPasswordET, secretAnswerET;
    private TextView errorMailTV, errorPasswordTV, errorPasswordVerifyTV, errorSecretQuestion, errorSecretAnswerTV;
    private Spinner secretQuestionSP;
    private Button resetPasswordBTN, signUpBTN;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    int step = 1;
    private int userId;
    private String currentMail, currentSecretQuestion, currentSecretAnswer, currentPassword;
    Drawable defaultBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();


        setFirstStep();

        resetPasswordBTN = findViewById(R.id.forgotPassword_resetPasswordBTN);
        resetPasswordBTN.setOnClickListener(v -> {
            resetPassword();
        });

        signUpBTN = findViewById(R.id.forgotPassword_signUpBTN);
        signUpBTN.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, CreateAccount.class);
            startActivity(intent);
        });
    }

    private void resetPassword() {
        if (ErrorManager.checkEmail(mailET.getText().toString())) {
            if (step == 1) {
                new CheckAccountTask().execute();
            } else if (step == 2) {
                checkStepQuestionAnswer();
            } else if (step == 3) {
                checkStepPassword();
            }
        } else {
            setFirstStep();
            errors.clear();
            errors.put(mailET, Errors.INVALID_MAIL_FORMAT);
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
                    userId = resultSet.getInt("Id");
                    currentMail = resultSet.getString("Email");
                    currentSecretQuestion = resultSet.getString("QuestionSecrete");
                    currentSecretAnswer = resultSet.getString("ReponseSecrete");
                    currentPassword = resultSet.getString("Password");
                    this.exists = true;
                } else {
                    this.exists = false;
                    Log.e("Database Connection", "Aucun compte avec cet email!");
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
                errors.clear();
                ErrorManager.updateBorder(ForgotPassword.this, errors, errorMessagesViews);
                setSecondStep();
            } else {
                errors.put(mailET, Errors.NO_ACCOUNT_FOUND);
                ErrorManager.updateBorder(ForgotPassword.this, errors, errorMessagesViews);
            }
        }
    }

    private class ResetPasswordTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "UPDATE user SET Password = ? WHERE Id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newPasswordET.getText().toString());
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();

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
            Toast.makeText(ForgotPassword.this, "Mot de passe réinitialisé avec succès!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ForgotPassword.this, Login.class);
            startActivity(intent);
        }

    }

    private void getViews() {
        mailET = findViewById(R.id.forgotPassword_mailET);
        newPasswordET = findViewById(R.id.forgotPassword_newPasswordET);
        verifyNewPasswordET = findViewById(R.id.forgotPassword_newPasswordVerifyET);
        secretQuestionSP = findViewById(R.id.forgotPassword_secretQuestionSP);
        secretAnswerET = findViewById(R.id.forgotPassword_secretAnswerET);

        defaultBackground = mailET.getBackground();

        errorMailTV = findViewById(R.id.forgotPassword_errorMailTV);
        errorPasswordTV = findViewById(R.id.forgotPassword_errorNewPasswordTV);
        errorPasswordVerifyTV = findViewById(R.id.forgotPassword_errorNewPasswordVerifyTV);
        errorSecretQuestion = findViewById(R.id.forgotPassword_errorSecretQuestionTV);
        errorSecretAnswerTV = findViewById(R.id.forgotPassword_errorSecretAnswerTV);


        errorMessagesViews.put(mailET, errorMailTV);
        errorMessagesViews.put(newPasswordET, errorPasswordTV);
        errorMessagesViews.put(verifyNewPasswordET, errorPasswordVerifyTV);
        errorMessagesViews.put(secretQuestionSP, errorSecretQuestion);
        errorMessagesViews.put(secretAnswerET, errorSecretAnswerTV);

        for (View view : errorMessagesViews.keySet()) {
            errorMessagesViews.get(view).setVisibility(View.GONE);
        }
    }

    private void setFirstStep() {
        step = 1;
        mailET.setVisibility(View.VISIBLE);
        mailET.setBackground(defaultBackground);
        secretQuestionSP.setVisibility(View.GONE);
        secretAnswerET.setVisibility(View.GONE);
        newPasswordET.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
    }

    private void setSecondStep() {
        step = 2;
        mailET.setVisibility(View.GONE);
        secretQuestionSP.setVisibility(View.VISIBLE);
        secretQuestionSP.setBackgroundResource(0);
        secretAnswerET.setVisibility(View.VISIBLE);
        secretAnswerET.setBackground(defaultBackground);
        newPasswordET.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
    }

    private void checkStepQuestionAnswer() {
        errors.clear();
        if (secretQuestionSP.getSelectedItem().toString().isEmpty()) {
            errors.put(secretQuestionSP, Errors.EMPTY_FIELD);
            errors.put(secretAnswerET, Errors.EMPTY);
        } else if (!secretQuestionSP.getSelectedItem().toString().equals(currentSecretQuestion)) {
            errors.put(secretQuestionSP, Errors.INVALID_QUESTION_ANSWER);
            errors.put(secretAnswerET, Errors.EMPTY);
        } else {
            if (secretAnswerET.getText().toString().isEmpty()) {
                errors.put(secretAnswerET, Errors.EMPTY_FIELD);
            } else if (!secretAnswerET.getText().toString().equals(currentSecretAnswer)) {
                errors.put(secretQuestionSP, Errors.INVALID_QUESTION_ANSWER);
                errors.put(secretAnswerET, Errors.EMPTY);
            }
        }

        ErrorManager.updateBorder(this, errors, errorMessagesViews);
        if (errors.isEmpty()) {
            setThirdStep();
        }
    }

    private void setThirdStep() {
        step = 3;
        mailET.setVisibility(View.GONE);
        secretQuestionSP.setVisibility(View.GONE);
        secretAnswerET.setVisibility(View.GONE);
        newPasswordET.setVisibility(View.VISIBLE);
        newPasswordET.setBackground(defaultBackground);
        verifyNewPasswordET.setVisibility(View.VISIBLE);
        verifyNewPasswordET.setBackground(defaultBackground);
    }

    private void checkStepPassword() {
        errors.clear();
        if (!ErrorManager.checkPassword(newPasswordET.getText().toString())) {
            errors.put(newPasswordET, Errors.INVALID_PASSWORD_FORMAT);
            errors.put(verifyNewPasswordET, Errors.EMPTY);
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        } else if (!ErrorManager.checkPasswordVerify(newPasswordET.getText().toString(), verifyNewPasswordET.getText().toString())) {
            errors.put(verifyNewPasswordET, Errors.INVALID_PASSWORD_CONFIRMATION);
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        } else if (newPasswordET.getText().toString().equals(currentPassword)) {
            errors.put(newPasswordET, Errors.INVALID_NEW_PASSWORD);
            errors.put(verifyNewPasswordET, Errors.EMPTY);
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
        } else {
            ErrorManager.updateBorder(this, errors, errorMessagesViews);
            new ResetPasswordTask().execute();
        }
    }
}


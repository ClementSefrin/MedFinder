package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mailET, newPasswordET, verifyNewPasswordET, secretAnswerET;
    private TextView errorMailTV, errorPasswordTV, errorPasswordVerifyTV, errorSecretQuestion, errorSecretAnswerTV;
    private LinearLayout passwordLL;
    private Spinner secretQuestionSP;
    private Button resetPasswordBTN, signUpBTN;
    private ImageButton passwordVisibilityBTN;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    int step = 1;
    private int userId, currentQuestionID;
    private String currentMail, currentSecretAnswer, currentPassword;
    Drawable defaultBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();
        setFirstStep();

        resetPasswordBTN.setOnClickListener(v -> {
            resetPassword();
        });

        passwordVisibilityBTN.setOnClickListener(v -> {
            changePasswordVisibility();
        });

        signUpBTN.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        newPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    newPasswordET.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    newPasswordET.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });

        verifyNewPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    verifyNewPasswordET.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    verifyNewPasswordET.setSelection(MedFind.getMaxCharLimit());
                }
            }
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
                    currentQuestionID = resultSet.getInt("QuestionSecrete");
                    currentSecretAnswer = resultSet.getString("ReponseSecrete");
                    currentPassword = resultSet.getString("Password");
                    this.exists = true;
                } else {
                    this.exists = false;
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
                ErrorManager.updateBorder(ForgotPasswordActivity.this, errors, errorMessagesViews);
                setSecondStep();
            } else {
                errors.put(mailET, Errors.NO_ACCOUNT_FOUND);
                ErrorManager.updateBorder(ForgotPasswordActivity.this, errors, errorMessagesViews);
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
            Toast.makeText(ForgotPasswordActivity.this, "Mot de passe réinitialisé avec succès!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void getViews() {
        mailET = findViewById(R.id.forgotPassword_mailET);
        newPasswordET = findViewById(R.id.forgotPassword_newPasswordET);
        verifyNewPasswordET = findViewById(R.id.forgotPassword_newPasswordVerifyET);
        secretQuestionSP = findViewById(R.id.forgotPassword_secretQuestionSP);
        secretAnswerET = findViewById(R.id.forgotPassword_secretAnswerET);

        resetPasswordBTN = findViewById(R.id.forgotPassword_resetPasswordBTN);
        signUpBTN = findViewById(R.id.forgotPassword_signUpBTN);
        passwordVisibilityBTN = findViewById(R.id.forgotPassword_passwordVisibilityBTN);

        passwordLL = findViewById(R.id.forgotPassword_passwordLL);

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
        passwordLL.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
        resetPasswordBTN.setText(R.string.forgotPasswordButtonTextAltStep);
    }

    private void setSecondStep() {
        step = 2;
        mailET.setVisibility(View.GONE);
        secretQuestionSP.setVisibility(View.VISIBLE);
        secretQuestionSP.setBackgroundResource(0);
        secretAnswerET.setVisibility(View.VISIBLE);
        secretAnswerET.setBackground(defaultBackground);
        passwordLL.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
    }

    private void checkStepQuestionAnswer() {
        errors.clear();

        int selectedPosition = secretQuestionSP.getSelectedItemPosition();
        String[] secretQuestionsArray = getResources().getStringArray(R.array.questionsSecretes);
        if (selectedPosition >= 0 && selectedPosition < secretQuestionsArray.length) {
            int questionResourceId = getResources().getIdentifier(
                    secretQuestionsArray[selectedPosition], "string", getPackageName());
        }
        if (selectedPosition != currentQuestionID || secretQuestionSP.getSelectedItem().toString().isEmpty()) {
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
        passwordLL.setVisibility(View.VISIBLE);
        newPasswordET.setBackground(defaultBackground);
        verifyNewPasswordET.setVisibility(View.VISIBLE);
        verifyNewPasswordET.setBackground(defaultBackground);
        resetPasswordBTN.setText(R.string.forgotPasswordHeader);
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

    private void changePasswordVisibility() {
        int passwordSelectionStart = newPasswordET.getSelectionStart();
        int passwordSelectionEnd = newPasswordET.getSelectionEnd();

        int passwordVerifySelectionStart = verifyNewPasswordET.getSelectionStart();
        int passwordVerifySelectionEnd = verifyNewPasswordET.getSelectionEnd();

        if (newPasswordET.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            newPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            verifyNewPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_hide_password);
        } else {
            newPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            verifyNewPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_show_password);
        }
        newPasswordET.setSelection(passwordSelectionStart, passwordSelectionEnd);
        verifyNewPasswordET.setSelection(passwordVerifySelectionStart, passwordVerifySelectionEnd);
    }
}


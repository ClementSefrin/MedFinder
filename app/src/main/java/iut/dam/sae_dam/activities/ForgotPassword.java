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

public class ForgotPassword extends AppCompatActivity {

    private EditText mailET, newPasswordET, verifyNewPasswordET, secretAnswerET;
    private Spinner secretQuestionSP;
    private Button resetPasswordBTN, signUpBTN;
    private List<EditText> editTexts;
    private List<View> validFields;
    private List<View> invalidFields;
    int step = 1;
    private int userId;
    private String currentMail, currentSecretQuestion, currentSecretAnswer, currentPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        invalidFields = new ArrayList<>();
        validFields = new ArrayList<>();

        ViewGroup layout = findViewById(R.id.forgotPasswordRL);
        editTexts = getAllEditTexts(layout);

        mailET = findViewById(R.id.forgotPassword_mailET);
        newPasswordET = findViewById(R.id.forgotPassword_newPasswordET);
        verifyNewPasswordET = findViewById(R.id.forgotPassword_newPasswordVerifyET);
        secretQuestionSP = findViewById(R.id.forgotPassword_secretQuestionSP);
        secretAnswerET = findViewById(R.id.forgotPassword_secretAnswerET);
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
        //TODO : généraliser la vérifications des champs
        if (checkEmail(mailET.getText().toString())) {
            if (step == 1) {
                new CheckAccountTask().execute();
            } else if (step == 2) {
                checkStepQuestionAnswer();
            } else if (step == 3) {
                checkStepPassword();
            }
        } else {
            Toast.makeText(this, "Email invalide!", Toast.LENGTH_SHORT).show();
            setFirstStep();
            invalidFields.clear();
            invalidFields.add(mailET);
            updateBorder();
        }
    }

    private void setFirstStep() {
        step = 1;
        mailET.setVisibility(View.VISIBLE);
        secretQuestionSP.setVisibility(View.GONE);
        secretAnswerET.setVisibility(View.GONE);
        newPasswordET.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
    }

    private void setSecondStep() {
        step = 2;
        mailET.setVisibility(View.GONE);
        secretQuestionSP.setVisibility(View.VISIBLE);
        secretAnswerET.setVisibility(View.VISIBLE);
        newPasswordET.setVisibility(View.GONE);
        verifyNewPasswordET.setVisibility(View.GONE);
    }

    private void setThirdStep() {
        step = 3;
        mailET.setVisibility(View.GONE);
        secretQuestionSP.setVisibility(View.GONE);
        secretAnswerET.setVisibility(View.GONE);
        newPasswordET.setVisibility(View.VISIBLE);
        verifyNewPasswordET.setVisibility(View.VISIBLE);
    }

    private void checkStepQuestionAnswer() {
        invalidFields.clear();
        validFields.clear();
        if (secretQuestionSP.getSelectedItem().toString().isEmpty()) {
            invalidFields.add(secretQuestionSP);
        } else if (!secretQuestionSP.getSelectedItem().toString().equals(currentSecretQuestion)) {
            invalidFields.add(secretQuestionSP);
        } else {
            validFields.add(secretQuestionSP);
        }

        if (secretAnswerET.getText().toString().isEmpty()) {
            invalidFields.add(secretAnswerET);
        } else if (!secretAnswerET.getText().toString().equals(currentSecretAnswer)) {
            invalidFields.add(secretAnswerET);
        } else {
            validFields.add(secretAnswerET);
        }
        updateBorder();

        if (invalidFields.isEmpty()) {
            setThirdStep();
        }
    }

    private void checkStepPassword() {
        invalidFields.clear();
        validFields.clear();
        switch (checkPassword(currentPassword, newPasswordET.getText().toString(), verifyNewPasswordET.getText().toString())) {
            case -1:
                invalidFields.add(newPasswordET);
                invalidFields.add(verifyNewPasswordET);
                updateBorder();
                break;
            case 1:
                validFields.add(newPasswordET);
                invalidFields.add(verifyNewPasswordET);
                updateBorder();
                break;
            case 2:
                invalidFields.add(newPasswordET);
                invalidFields.add(verifyNewPasswordET);
                updateBorder();
                break;
            case 0:
                validFields.add(newPasswordET);
                validFields.add(verifyNewPasswordET);
                new ResetPasswordTask().execute();
                break;
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
            //TODO : généraliser les messages Toast (langue)
            if (exists) {
                invalidFields.clear();
                validFields.clear();
                validFields.add(mailET);
                updateBorder();
                setSecondStep();
            } else {
                invalidFields.clear();
                invalidFields.add(mailET);
                updateBorder();
                Toast.makeText(ForgotPassword.this, "Aucun compte trouvé avec cet email!", Toast.LENGTH_SHORT).show();
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

    private void allFieldsWrong() {
        invalidFields.clear();
        for (EditText field : editTexts) {
            invalidFields.add(field);
        }
        invalidFields.add(secretQuestionSP);

    }

    private void updateBorder() {
        for (EditText field : editTexts) {
            if (invalidFields.contains(field)) {
                field.setBackgroundResource(R.drawable.invalid_edittext_border);
            } else if (validFields.contains(field)) {
                field.setBackgroundResource(R.drawable.valid_edittext_border);
            }
        }
        if (invalidFields.contains(secretQuestionSP)) {
            secretQuestionSP.setBackgroundResource(R.drawable.invalid_edittext_border);
        } else if (validFields.contains(secretQuestionSP)) {
            secretQuestionSP.setBackgroundResource(R.drawable.valid_edittext_border);
        }
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

    private boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * Renvoie -1 si le mot de passe est vide, 1 si les mots de passe ne correspondent pas,
     * 2 si le mot de passe est le même que l'ancien, 0 sinon
     */
    private int checkPassword(String originalPassword, String newPassword, String newPasswordVerify) {
        return newPassword.isEmpty() ? -1 : !newPasswordVerify.equals(newPassword) ? 1 : newPassword.equals(originalPassword) ? 2 : 0;
    }
}


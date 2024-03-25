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
    private List<View> invalidFields;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        invalidFields = new ArrayList<>();

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
        mailET = findViewById(R.id.forgotPassword_mailET);
        newPasswordET = findViewById(R.id.forgotPassword_newPasswordET);
        verifyNewPasswordET = findViewById(R.id.forgotPassword_newPasswordVerifyET);
        secretQuestionSP = findViewById(R.id.forgotPassword_secretQuestionSP);
        secretAnswerET = findViewById(R.id.forgotPassword_secretAnswerET);
        //TODO : généraliser la vérifications des champs

        ViewGroup layout = findViewById(R.id.forgotPasswordRL);
        editTexts = getAllEditTexts(layout);
        if (checkEmail(mailET.getText().toString())) {
            invalidFields.clear();
            new CheckAccountTask().execute();
        } else {
            Toast.makeText(this, "Email invalide!", Toast.LENGTH_SHORT).show();
            invalidFields.clear();
            allFieldsWrong();
            updateBorder();
        }

    }


    private class CheckAccountTask extends AsyncTask<Void, Void, Void> {
        private boolean exists;
        private int userId;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();
                String query = "SELECT * FROM user WHERE Email = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mailET.getText().toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Log.e("Database Connection", "Un compte existe bien!");
                    userId = resultSet.getInt("Id");
                    this.exists = true;
                } else {
                    this.exists = false;
                    Log.e("Database Connection", "Aucun compte avec cet email!");
                }

                if (exists) {
                    if (!resultSet.getString("QuestionSecrete").equals(secretQuestionSP.getSelectedItem().toString())) {
                        invalidFields.add(secretQuestionSP);
                    }
                    if (!resultSet.getString("ReponseSecrete").equals(secretAnswerET.getText().toString())) {
                        invalidFields.add(secretAnswerET);
                    }
                    switch (checkPassword(resultSet.getString("Password"), newPasswordET.getText().toString(), verifyNewPasswordET.getText().toString())) {
                        case -1:
                            invalidFields.add(newPasswordET);
                            invalidFields.add(verifyNewPasswordET);
                            break;
                        case 1:
                            invalidFields.add(verifyNewPasswordET);
                            break;
                        case 2:
                            invalidFields.add(newPasswordET);
                            invalidFields.add(verifyNewPasswordET);
                            break;
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
                if (invalidFields.isEmpty()) {
                    invalidFields.clear();
                    updateBorder();
                    new ResetPasswordTask(userId).execute();
                } else {
                    updateBorder();
                    Toast.makeText(ForgotPassword.this, "Certains champs ne sont pas valides!", Toast.LENGTH_SHORT).show();
                }
            } else {
                allFieldsWrong();
                updateBorder();
                Log.e("Database Connection", "Aucun compte trouvé avec cet email!");
                //TODO : généraliser les messages Toast (langue)
                Toast.makeText(ForgotPassword.this, "Aucun compte trouvé avec cet email!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ResetPasswordTask extends AsyncTask<Void, Void, Void> {

        private int userId;

        public ResetPasswordTask(int userId) {
            super();
            this.userId = userId;
        }

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

            Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
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
            } else {
                field.setBackgroundResource(R.drawable.valid_edittext_border);
            }
        }
        if (invalidFields.contains(secretQuestionSP)) {
            secretQuestionSP.setBackgroundResource(R.drawable.invalid_edittext_border);
        } else {
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


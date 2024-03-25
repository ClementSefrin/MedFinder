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

import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.R;

public class CreateAccount extends AppCompatActivity {
    EditText firstNameET, lastNameET, mailET, cityET, passwordET, passwordVerifyEt, secretAnswerET;
    Spinner secretQuestionSP;
    Button signUpBTN, Btn_AlreadyAccount;
    private List<EditText> editTexts;
    private List<View> invalidFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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

    private void createAccount() {
        firstNameET = findViewById(R.id.createAccount_firstNameET);
        lastNameET = findViewById(R.id.createAccount_lastNameET);
        mailET = findViewById(R.id.createAccount_mailET);
        cityET = findViewById(R.id.createAccount_cityET);
        passwordET = findViewById(R.id.createAccount_passwordET);
        passwordVerifyEt = findViewById(R.id.createAccount_verifyPasswordET);
        secretQuestionSP = findViewById(R.id.createAccount_secretQuestionSP);
        secretAnswerET = findViewById(R.id.createAccount_secretAnswerET);
        signUpBTN = findViewById(R.id.createAccount_signUpBTN);

        ViewGroup layout = findViewById(R.id.createAccountRL);
        editTexts = getAllEditTexts(layout);
        invalidFields = getInvalidFields();
        if (invalidFields.isEmpty()) {
            new CheckAccountTask().execute();
        } else {
            Toast.makeText(CreateAccount.this, "Certains champs ne sont pas valides!", Toast.LENGTH_SHORT).show();
            updateBorder();
        }
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

    private List<View> getInvalidFields() {
        List<View> invalidFields = new ArrayList<>();
        if (firstNameET.getText().toString().isEmpty()) {
            invalidFields.add(firstNameET);
        }
        if (lastNameET.getText().toString().isEmpty()) {
            invalidFields.add(lastNameET);
        }
        if (cityET.getText().toString().isEmpty()) {
            invalidFields.add(cityET);
        }
        switch (checkPassword(passwordET.getText().toString(), passwordVerifyEt.getText().toString())) {
            case -1:
                invalidFields.add(passwordET);
                invalidFields.add(passwordVerifyEt);
                break;
            case 1:
                invalidFields.add(passwordVerifyEt);
                break;
            default:
                break;
        }
        if (!checkEmail(mailET.getText().toString())) {
            invalidFields.add(mailET);
        }
        if (secretQuestionSP.getSelectedItemPosition() == 0) {
            invalidFields.add(secretQuestionSP);
        }
        if (secretAnswerET.getText().toString().isEmpty()) {
            invalidFields.add(secretAnswerET);
        }
        return invalidFields;
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
                invalidFields.add(mailET);
                updateBorder();
                Toast.makeText(CreateAccount.this, "Un compte existe déjà avec cet email!", Toast.LENGTH_SHORT).show();
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
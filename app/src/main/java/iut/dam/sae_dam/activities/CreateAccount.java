package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    EditText EditText_FirstName, EditText_SecondName, EditText_Mail, EditText_City, EditText_Mdp, EditText_MdpVerify;
    Button Btn_Validation, Btn_AlreadyAccount;
    private List<EditText> invalidFields, editTexts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Btn_AlreadyAccount = findViewById(R.id.createAccount_alreadySignedUpBTN);
        Btn_AlreadyAccount.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, Login.class);
            startActivity(intent);
        });

        Btn_Validation = findViewById(R.id.createAccount_signUpBTN);
        Btn_Validation.setOnClickListener(v -> {
            createAccount();
        });
    }

    private void createAccount() {
        EditText_FirstName = findViewById(R.id.createAccount_firstNameET);
        EditText_SecondName = findViewById(R.id.createAccount_lastNameET);
        EditText_Mail = findViewById(R.id.createAccount_mailET);
        EditText_City = findViewById(R.id.createAccount_cityET);
        EditText_Mdp = findViewById(R.id.createAccount_passwordET);
        EditText_MdpVerify = findViewById(R.id.createAccount_verifyPasswordET);
        Btn_Validation = findViewById(R.id.createAccount_signUpBTN);

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

    private List<EditText> getInvalidFields() {
        List<EditText> invalidFields = new ArrayList<>();
        if (EditText_FirstName.getText().toString().isEmpty()) {
            invalidFields.add(EditText_FirstName);
        }
        if (EditText_SecondName.getText().toString().isEmpty()) {
            invalidFields.add(EditText_SecondName);
        }
        if (EditText_City.getText().toString().isEmpty()) {
            invalidFields.add(EditText_City);
        }
        switch (checkPassword(EditText_Mdp.getText().toString(), EditText_MdpVerify.getText().toString())) {
            case -1:
                invalidFields.add(EditText_Mdp);
                invalidFields.add(EditText_MdpVerify);
                break;
            case 1:
                invalidFields.add(EditText_MdpVerify);
                break;
            default:
                break;
        }
        if (!checkEmail(EditText_Mail.getText().toString())) {
            invalidFields.add(EditText_Mail);
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
                preparedStatement.setString(1, EditText_Mail.getText().toString());
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
                invalidFields.add(EditText_Mail);
                updateBorder();
                Toast.makeText(CreateAccount.this, "Un compte existe déjà avec cet email!", Toast.LENGTH_SHORT).show();
            } else {
                new CreateAccountTask().execute();
            }
        }
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "INSERT INTO user (FirstName, SecondName, Email, Password, Administrator, City) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, EditText_FirstName.getText().toString());
                preparedStatement.setString(2, EditText_SecondName.getText().toString());
                preparedStatement.setString(3, EditText_Mail.getText().toString());
                preparedStatement.setString(4, EditText_Mdp.getText().toString());
                preparedStatement.setInt(5, 1);
                preparedStatement.setString(6, EditText_City.getText().toString());
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
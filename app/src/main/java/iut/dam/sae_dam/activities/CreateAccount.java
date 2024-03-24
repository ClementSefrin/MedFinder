package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.R;

public class CreateAccount extends AppCompatActivity {
    EditText editText_FirstName, editText_SecondName, EditText_Mail, EditText_City, EditText_Mdp, EditText_MdpVerify;
    Button Btn_Validation, Btn_AlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Btn_AlreadyAccount = findViewById(R.id.Btn_AlreadyAccount);
        Btn_AlreadyAccount.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, Login.class);
            startActivity(intent);
        });

        Btn_Validation = findViewById(R.id.Btn_Validation);
        Btn_Validation.setOnClickListener(v -> {
            createAccount();
        });
    }

    private void createAccount() {
        editText_FirstName = findViewById(R.id.editText_FirstName);
        editText_SecondName = findViewById(R.id.editText_SecondName);
        EditText_Mail = findViewById(R.id.EditText_Mail);
        EditText_City = findViewById(R.id.EditText_City);
        EditText_Mdp = findViewById(R.id.EditText_Mdp);
        EditText_MdpVerify = findViewById(R.id.EditText_MdpVerify);
        Btn_Validation = findViewById(R.id.Btn_Validation);

        List<String> invalidFields = getInvalidFields();
        if (invalidFields.isEmpty()) {
            new CreateAccountTask().execute();
        } else {
            Toast.makeText(CreateAccount.this, "Certains champs ne sont pas valides!", Toast.LENGTH_SHORT).show();
            for (String fieldName : invalidFields) {
                setFieldInvalid(fieldName);
            }
        }
    }

    private class CreateAccountTask extends AsyncTask<Void, Void, Void> {
        private List<String> invalidFields;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "INSERT INTO user (FirstName, SecondName, Email, Password, Administrator, City) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, editText_FirstName.getText().toString());
                preparedStatement.setString(2, editText_SecondName.getText().toString());
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
            startActivity(intent);
        }
    }

    private List<String> getInvalidFields() {
        List<String> invalidFields = new ArrayList<>();
        if (editText_FirstName.getText().toString().isEmpty()) {
            invalidFields.add("Prénom");
        } else {
            editText_FirstName.setBackgroundResource(android.R.drawable.edit_text);
        }
        if (editText_SecondName.getText().toString().isEmpty()) {
            invalidFields.add("Nom");
        } else {
            editText_SecondName.setBackgroundResource(android.R.drawable.edit_text);
        }
        if (EditText_City.getText().toString().isEmpty()) {
            invalidFields.add("Ville");
        } else {
            EditText_City.setBackgroundResource(android.R.drawable.edit_text);
        }
        if (EditText_Mdp.getText().toString().isEmpty()) {
            invalidFields.add("Mot de passe");
        }
        if (EditText_MdpVerify.getText().toString().isEmpty()) {
            invalidFields.add("Mot de passe");
        }
        if (!isValidEmail(EditText_Mail.getText().toString())) {
            invalidFields.add("Email");
        } else {
            EditText_Mail.setBackgroundResource(android.R.drawable.edit_text);
        }
        return invalidFields;
    }

    private void setFieldInvalid(String fieldName) {
        switch (fieldName) {
            case "Prénom":
                editText_FirstName.setBackgroundResource(R.drawable.invalid_edittext_border);
                break;
            case "Nom":
                editText_SecondName.setBackgroundResource(R.drawable.invalid_edittext_border);
                break;
            case "Ville":
                EditText_City.setBackgroundResource(R.drawable.invalid_edittext_border);
                break;
            case "Mot de passe":
                EditText_Mdp.setBackgroundResource(R.drawable.invalid_edittext_border);
                EditText_MdpVerify.setBackgroundResource(R.drawable.invalid_edittext_border);
                break;
            case "Email":
                EditText_Mail.setBackgroundResource(R.drawable.invalid_edittext_border);
                break;
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void validateEmail() {
        String email = EditText_Mail.getText().toString();
        if (isValidEmail(email)) {
            EditText_Mail.setBackgroundResource(android.R.drawable.edit_text);
        } else {
            EditText_Mail.setBackgroundResource(R.drawable.invalid_edittext_border);
        }
    }

    private boolean MdpVerify(String mdp, String mdpVerify) {
        return mdp.length() != 0 && mdpVerify.length() != 0;
    }

    private void validateMdp() {
        String mdp = EditText_Mdp.getText().toString();
        String mdpVerify = EditText_MdpVerify.getText().toString();
        if (MdpVerify(mdp, mdpVerify)) {
            EditText_Mdp.setBackgroundResource(android.R.drawable.edit_text);
            EditText_MdpVerify.setBackgroundResource(android.R.drawable.edit_text);
        } else {
            EditText_Mdp.setBackgroundResource(R.drawable.invalid_edittext_border);
            EditText_MdpVerify.setBackgroundResource(R.drawable.invalid_edittext_border);
        }
    }
}
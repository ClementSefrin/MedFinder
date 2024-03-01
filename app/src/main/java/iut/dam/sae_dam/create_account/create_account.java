package iut.dam.sae_dam.create_account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.DatabaseConnection;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.login.login;

public class create_account extends AppCompatActivity {
    Connection connect;
    EditText editText_FirstName;
    EditText editText_SecondName;
    EditText EditText_Mail;
    EditText EditText_City;
    EditText EditText_Mdp;
    EditText EditText_MdpVerify;
    Button Btn_Validation;
    Button Btn_AlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        new DatabaseTask().execute();
        Btn_AlreadyAccount = findViewById(R.id.Btn_AlreadyAccount);
        Btn_AlreadyAccount.setOnClickListener(v -> {
            Intent intent = new Intent(create_account.this, login.class);
            startActivity(intent);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class DatabaseTask extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... voids) {
            try {
                return DatabaseConnection.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    
        @Override
        protected void onPostExecute(Connection connection) {
            super.onPostExecute(connection);
            editText_FirstName = findViewById(R.id.editText_FirstName);
            editText_SecondName = findViewById(R.id.editText_SecondName);
            EditText_Mail = findViewById(R.id.EditText_Mail);
            EditText_City = findViewById(R.id.EditText_City);
            EditText_Mdp = findViewById(R.id.EditText_Mdp);
            EditText_MdpVerify = findViewById(R.id.EditText_MdpVerify);
            Btn_Validation = findViewById(R.id.Btn_Validation);

            // addTextChangedListener pour vérifier si le champ est vide
            // et pour vérifier si le champ est valide
            EditText_Mail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    validateEmail();
                    validateMdp();
                }
            });
            // SDK_INT > 8 pour permettre la connexion à la base de données
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                // Permettre la connexion à la base de données
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                // Si la connexion est réussie, on peut créer un compte
                if (connection != null) {
                    Toast.makeText(create_account.this, "Connexion à la base de données réussie!", Toast.LENGTH_SHORT).show();
                    Btn_Validation.setOnClickListener(v -> {
                        String email = EditText_Mail.getText().toString();
                        connect = connection;
                        // Requête SQL pour insérer les données dans la base de données
                        String query = "INSERT INTO user (FirstName, SecondName, Email, Password, Administrator, City) VALUES " +
                                "('" + editText_FirstName.getText().toString() + "', '" +
                                editText_SecondName.getText().toString() + "', '" + EditText_Mail.getText().toString() + "', '" +
                                EditText_Mdp.getText().toString() + "', '" + 1 + "', '" +
                                EditText_City.getText().toString() + "')";

                        List<String> invalidFields = getInvalidFields();
                        // Si les champs sont valides, on peut insérer les données dans la base de données
                        if (invalidFields.isEmpty()) {
                            // Exécution de la requête SQL obligatoire dans un try-catch
                            try {
                                // Exécution de la requête SQL
                                Statement statement = connect.createStatement();
                                statement.executeUpdate(query);
                                Toast.makeText(create_account.this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            for (String fieldName : invalidFields) {
                                setFieldInvalid(fieldName);
                            }
                            Toast.makeText(create_account.this, "Certains champs ne sont pas valides!", Toast.LENGTH_SHORT).show();
                        }
                        DatabaseConnection.closeConnection(connection);
                    });
                } else {
                    Toast.makeText(create_account.this, "Échec de la connexion à la base de données!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private List<String> getInvalidFields() {
        List<String> invalidFields = new ArrayList<>();
        if (editText_FirstName.getText().toString().isEmpty()) {
            invalidFields.add("Prénom");
        }
        else {
            editText_FirstName.setBackgroundResource(android.R.drawable.edit_text);
        }
        if (editText_SecondName.getText().toString().isEmpty()) {
            invalidFields.add("Nom");
        }
        else {
            editText_SecondName.setBackgroundResource(android.R.drawable.edit_text);
        }
        if (EditText_City.getText().toString().isEmpty()) {
            invalidFields.add("Ville");
        }
        else {
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
        }
        else {
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

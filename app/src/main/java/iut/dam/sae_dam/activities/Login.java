package iut.dam.sae_dam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import iut.dam.sae_dam.R;


public class Login extends AppCompatActivity {

    EditText EditText_Mail;
    EditText EditText_Mdp;

    Button Btn_Inscription;
    Button Btn_forget_password;
    Button Btn_Connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText_Mail = findViewById(R.id.login_mailET);
        EditText_Mdp = findViewById(R.id.login_passwordET);
        Btn_Inscription = findViewById(R.id.login_noAccountBTN);
        Btn_forget_password = findViewById(R.id.login_forgotPasswordBTN);
        Btn_Connexion = findViewById(R.id.login_logInBTN);

        Btn_forget_password.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });

        Btn_Inscription.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, CreateAccount.class);
            startActivity(intent);
        });

        Btn_Connexion.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });
    }
}

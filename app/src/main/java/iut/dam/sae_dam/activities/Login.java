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

        EditText_Mail = findViewById(R.id.createAccount_mailET);
        EditText_Mdp = findViewById(R.id.createAccount_passwordET);
        Btn_Inscription = findViewById(R.id.createAccount_alreadySignedUpBTN);
        Btn_forget_password = findViewById(R.id.Btn_forget_password);
        Btn_Connexion = findViewById(R.id.Btn_Connexion);
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

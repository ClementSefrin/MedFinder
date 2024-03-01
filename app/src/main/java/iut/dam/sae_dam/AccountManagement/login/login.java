package iut.dam.sae_dam.AccountManagement.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import iut.dam.sae_dam.MainActivity;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.AccountManagement.create_account.create_account;


public class login extends AppCompatActivity {

    EditText EditText_Mail;
    EditText EditText_Mdp;

    Button Btn_Inscription;
    Button Btn_forget_password;
    Button Btn_Connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText_Mail = findViewById(R.id.EditText_Mail);
        EditText_Mdp = findViewById(R.id.EditText_Mdp);
        Btn_Inscription = findViewById(R.id.Btn_AlreadyAccount);
        Btn_forget_password = findViewById(R.id.Btn_forget_password);
        Btn_Connexion = findViewById(R.id.Btn_Connexion);
        Btn_Inscription.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, create_account.class);
            startActivity(intent);
        });

        Btn_Connexion.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
        });
    }
}

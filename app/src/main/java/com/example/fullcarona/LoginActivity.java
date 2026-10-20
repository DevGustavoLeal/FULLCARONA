package com.example.fullcarona;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnEntrar;
    private TextView txtEsqueciSenha;
    private static final String PREFS = "fullcarona_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGADO = "logado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.login_navy_bg));

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        edtEmail.setText(prefs.getString(KEY_EMAIL, ""));

        btnEntrar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Informe o e-mail");
                edtEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(senha)) {
                edtSenha.setError("Informe a senha");
                edtSenha.requestFocus();
                return;
            }

            prefs.edit()
                    .putString(KEY_EMAIL, email)
                    .putBoolean(KEY_LOGADO, true)
                    .apply();

            Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show();
        });

        txtEsqueciSenha.setOnClickListener(v ->
                Toast.makeText(this, "Recuperar senha", Toast.LENGTH_SHORT).show());
    }
}
package com.example.fullcarona;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnEntrar;
    private TextView txtEsqueciSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.login_navy_bg));

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

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

            Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show();
        });

        txtEsqueciSenha.setOnClickListener(v ->
                Toast.makeText(this, "Recuperar senha", Toast.LENGTH_SHORT).show());
    }
}
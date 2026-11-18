package com.example.fullcarona;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Usuario;
import com.example.fullcarona.data.UsuarioRepository;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtSenha;
    private AppCompatButton btnEntrar;
    private TextView txtEsqueciSenha, txtCriarConta;
    private static final String PREFS = "fullcarona_prefs";
    private static final String KEY_EMAIL = "email";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(getResources().getColor(R.color.login_navy_bg));
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);
        txtCriarConta = findViewById(R.id.txtCriarConta);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        edtEmail.setText(prefs.getString(KEY_EMAIL, ""));
        btnEntrar.setOnClickListener(v -> autenticar(prefs));
        txtCriarConta.setOnClickListener(v -> startActivity(new Intent(this, CriarContaActivity.class)));
        txtEsqueciSenha.setOnClickListener(v -> abrirRedefinicaoSenha());
    }

    private void autenticar(SharedPreferences prefs) {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        if (TextUtils.isEmpty(email)) { edtEmail.setError("Informe o e-mail"); edtEmail.requestFocus(); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError("Informe um e-mail válido"); edtEmail.requestFocus(); return; }
        if (TextUtils.isEmpty(senha)) { edtSenha.setError("Informe a senha"); edtSenha.requestFocus(); return; }
        prefs.edit().putString(KEY_EMAIL, email).apply();
        try {
            Usuario usuario = new UsuarioRepository(AppDatabase.getInstance(this)).login(email, senha);
            if (usuario == null) {
                edtSenha.setError("Confira o e-mail e a senha");
                edtSenha.requestFocus();
                Toast.makeText(this, "E-mail ou senha inválidos.", Toast.LENGTH_SHORT).show();
                return;
            }
            new Sessao(this).salvarUsuario(usuario.id);
            Navegacao.abrirPosLogin(this, usuario);
            finish();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Não foi possível acessar a conta. Tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    private void abrirRedefinicaoSenha() {
        EditText email = new EditText(this);
        email.setHint("E-mail da conta");
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText senha = new EditText(this);
        senha.setHint("Nova senha (mínimo 6 caracteres)");
        senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        android.widget.LinearLayout campos = new android.widget.LinearLayout(this);
        campos.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = TelaUi.dp(this, 20);
        campos.setPadding(padding, 0, padding, 0);
        campos.addView(email);
        campos.addView(senha);
        new AlertDialog.Builder(this).setTitle("Redefinir senha")
                .setMessage("A senha será atualizada neste dispositivo.")
                .setView(campos)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String e = email.getText().toString().trim();
                    String s = senha.getText().toString();
                    try {
                        boolean atualizou = new UsuarioRepository(AppDatabase.getInstance(this)).redefinirSenha(e, s);
                        Toast.makeText(this, atualizou ? "Senha atualizada." : "E-mail não encontrado ou senha muito curta.", Toast.LENGTH_LONG).show();
                    } catch (RuntimeException ex) {
                        Toast.makeText(this, "Não foi possível redefinir a senha.", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }
}

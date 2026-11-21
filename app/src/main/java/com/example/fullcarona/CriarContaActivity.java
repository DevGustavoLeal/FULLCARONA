package com.example.fullcarona;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Usuario;
import com.example.fullcarona.data.UsuarioRepository;

public class CriarContaActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = TelaUi.criar(this, "Criar conta", "Use seu e-mail e os dados acadêmicos para começar.");
        EditText nome = TelaUi.campo(this, root, "Nome completo", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText email = TelaUi.campo(this, root, "E-mail", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText instituicao = TelaUi.campo(this, root, "Instituição (ex: Fucapi, UFAM, UEA)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText curso = TelaUi.campo(this, root, "Curso", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText ra = TelaUi.campo(this, root, "RA / Matrícula acadêmica", InputType.TYPE_CLASS_TEXT);
        EditText senha = TelaUi.campo(this, root, "Senha (mínimo 6 caracteres)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText confirmar = TelaUi.campo(this, root, "Confirme a senha", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        TelaUi.botao(this, root, "Criar conta", () -> {
            String e = email.getText().toString().trim();
            String s = senha.getText().toString();
            String inst = instituicao.getText().toString().trim();
            String matricula = ra.getText().toString().trim();

            if (nome.getText().toString().trim().isEmpty()) { nome.setError("Informe seu nome"); return; }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { email.setError("Informe um e-mail válido"); return; }
            if (inst.isEmpty()) { instituicao.setError("Informe sua instituição de ensino"); return; }
            if (curso.getText().toString().trim().isEmpty()) { curso.setError("Informe seu curso"); return; }
            if (matricula.isEmpty()) { ra.setError("Informe seu RA ou Matrícula"); return; }
            if (s.length() < 6) { senha.setError("Use pelo menos 6 caracteres"); return; }
            if (!s.equals(confirmar.getText().toString())) { confirmar.setError("As senhas não conferem"); return; }
            try {
                Usuario usuario = new Usuario(nome.getText().toString().trim(), e, null, curso.getText().toString().trim(), null);
                usuario.instituicao = inst;
                usuario.ra = matricula;
                usuario.senha = s;
                new UsuarioRepository(AppDatabase.getInstance(this)).cadastrar(usuario);
                getSharedPreferences("fullcarona_prefs", MODE_PRIVATE).edit().putString("email", e).apply();
                Toast.makeText(this, "Conta criada. Agora entre com seu e-mail e senha.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } catch (IllegalArgumentException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            } catch (RuntimeException ex) {
                Toast.makeText(this, "Não foi possível criar a conta.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

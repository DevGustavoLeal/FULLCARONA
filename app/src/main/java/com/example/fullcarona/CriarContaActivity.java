package com.example.fullcarona;

import android.app.AlertDialog;
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
    private EditText instituicao;
    private EditText curso;

    private static final String[] INSTITUICOES = {
            "FUCAPI",
            "UFAM",
            "UEA",
            "Fametro",
            "Uninorte",
            "Nilton Lins",
            "Outro (digitar)..."
    };

    private static final String[] CURSOS = {
            "Engenharia de Software",
            "Ciência da Computação",
            "Engenharia da Computação",
            "Sistemas de Informação",
            "Análise e Desenvolvimento de Sistemas",
            "Engenharia Elétrica",
            "Engenharia Mecânica",
            "Engenharia de Produção",
            "Engenharia Civil",
            "Administração",
            "Contabilidade",
            "Direito",
            "Enfermagem",
            "Psicologia",
            "Outro (digitar)..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = TelaUi.criar(this, "Criar conta", "Use seu e-mail e os dados acadêmicos para começar.");

        EditText nome = TelaUi.campo(this, root, "Nome completo", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText email = TelaUi.campo(this, root, "E-mail", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        instituicao = TelaUi.campo(this, root, "Instituição (Toque para selecionar)", InputType.TYPE_NULL);
        instituicao.setFocusable(false);
        instituicao.setClickable(true);
        instituicao.setOnClickListener(v -> selecionarOpcao("Selecione a instituição", INSTITUICOES, instituicao));

        curso = TelaUi.campo(this, root, "Curso (Toque para selecionar)", InputType.TYPE_NULL);
        curso.setFocusable(false);
        curso.setClickable(true);
        curso.setOnClickListener(v -> selecionarOpcao("Selecione o curso", CURSOS, curso));

        EditText ra = TelaUi.campo(this, root, "RA / Matrícula acadêmica", InputType.TYPE_CLASS_TEXT);
        EditText senha = TelaUi.campo(this, root, "Senha (mínimo 6 caracteres)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText confirmar = TelaUi.campo(this, root, "Confirme a senha", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        TelaUi.botao(this, root, "Criar conta", () -> {
            String n = nome.getText().toString().trim();
            String e = email.getText().toString().trim();
            String inst = instituicao.getText().toString().trim();
            String cur = curso.getText().toString().trim();
            String matricula = ra.getText().toString().trim();
            String s = senha.getText().toString();
            String conf = confirmar.getText().toString();

            if (n.isEmpty()) {
                nome.setError("Informe seu nome");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.setError("Informe um e-mail válido");
                return;
            }
            if (inst.isEmpty()) {
                instituicao.setError("Selecione sua instituição de ensino");
                Toast.makeText(this, "Selecione sua instituição.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cur.isEmpty()) {
                curso.setError("Selecione seu curso");
                Toast.makeText(this, "Selecione seu curso.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (matricula.isEmpty()) {
                ra.setError("Informe seu RA ou Matrícula");
                return;
            }
            if (s.length() < 6) {
                senha.setError("Use pelo menos 6 caracteres");
                return;
            }
            if (!s.equals(conf)) {
                confirmar.setError("As senhas não conferem");
                return;
            }

            try {
                Usuario usuario = new Usuario(n, e, null, cur, null);
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

    private void selecionarOpcao(String titulo, String[] opcoes, EditText destinoCampo) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(opcoes, (dialog, which) -> {
                    String escolhida = opcoes[which];
                    if (escolhida.startsWith("Outro")) {
                        pedirTextoManual(titulo, destinoCampo);
                    } else {
                        destinoCampo.setError(null);
                        destinoCampo.setText(escolhida);
                    }
                })
                .show();
    }

    private void pedirTextoManual(String titulo, EditText destinoCampo) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(input)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String valor = input.getText().toString().trim();
                    if (!valor.isEmpty()) {
                        destinoCampo.setError(null);
                        destinoCampo.setText(valor);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
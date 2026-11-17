package com.example.fullcarona;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Usuario;
import com.example.fullcarona.data.UsuarioRepository;

public class EscolhaPerfilActivity extends AppCompatActivity {
    private long usuarioId;
    private UsuarioRepository usuarios;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        usuarios = new UsuarioRepository(AppDatabase.getInstance(this));
        LinearLayout root = TelaUi.criar(this, "Você é?", "Escolha como deseja usar o FullCarona.");
        TelaUi.botao(this, root, "Passageiro", () -> escolher("PASSAGEIRO"));
        TelaUi.botao(this, root, "Motorista", () -> escolher("MOTORISTA"));
        TelaUi.botao(this, root, "Sair", () -> { new Sessao(this).sair(); finish(); });
    }
    private void escolher(String perfil) {
        if (!usuarios.definirPerfil(usuarioId, perfil)) {
            Toast.makeText(this, "Não foi possível salvar o perfil.", Toast.LENGTH_SHORT).show();
            return;
        }
        Usuario usuario = usuarios.buscarPorId(usuarioId);
        Navegacao.abrirPosLogin(this, usuario);
        finish();
    }
}

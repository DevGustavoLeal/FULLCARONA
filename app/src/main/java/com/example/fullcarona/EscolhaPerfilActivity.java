package com.example.fullcarona;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Usuario;
import com.example.fullcarona.data.UsuarioRepository;

public class EscolhaPerfilActivity extends AppCompatActivity {
    private long usuarioId;
    private UsuarioRepository usuarios;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat bars = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        bars.hide(WindowInsetsCompat.Type.systemBars());
        bars.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        setContentView(R.layout.activity_escolha_perfil);

        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        usuarios = new UsuarioRepository(AppDatabase.getInstance(this));
        AppCompatButton motorista = findViewById(R.id.btnMotorista);
        AppCompatButton passageiro = findViewById(R.id.btnPassageiro);
        motorista.setOnClickListener(view -> escolher("MOTORISTA"));
        passageiro.setOnClickListener(view -> escolher("PASSAGEIRO"));
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

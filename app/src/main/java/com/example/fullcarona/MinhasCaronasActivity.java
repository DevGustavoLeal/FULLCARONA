package com.example.fullcarona;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.Usuario;

public class MinhasCaronasActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        AppDatabase db = AppDatabase.getInstance(this);
        Usuario usuario = db.usuarioDao().buscarPorId(usuarioId);
        LinearLayout root = TelaUi.criar(this, "Minhas caronas", "Histórico e status das suas viagens.");
        if (usuario != null && "MOTORISTA".equals(usuario.perfil)) {
            for (Carona carona : db.caronaDao().listarDoMotorista(usuarioId)) {
                TelaUi.adicionarLinha(this, root, carona.origem + " → " + carona.destino + "\n"
                        + DataHoraUtil.exibir(carona.dataHora) + " · " + carona.vagas + " vaga(s) · " + carona.status);
            }
        } else {
            for (Solicitacao pedido : db.solicitacaoDao().listarDoPassageiro(usuarioId)) {
                Carona carona = db.caronaDao().buscarPorId(pedido.caronaId);
                if (carona != null) TelaUi.adicionarLinha(this, root, carona.origem + " → " + carona.destino + "\n"
                        + DataHoraUtil.exibir(carona.dataHora) + " · solicitação " + pedido.status);
            }
        }
        TelaUi.botao(this, root, "Voltar", this::finish);
    }
}
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullcarona.util.Sessao;

import java.util.ArrayList;
import java.util.List;

public class MinhasCaronasActivity extends AppCompatActivity {

    private RecyclerView recyclerMinhasCaronas;
    private Sessao sessao;
    private MinhasCaronasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_caronas);

        // Inicializa a sessão
        sessao = new Sessao(this);
        long usuarioId = sessao.getUsuarioId();

        // Referência do RecyclerView no layout
        recyclerMinhasCaronas = findViewById(R.id.recyclerMinhasCaronas);
        recyclerMinhasCaronas.setLayoutManager(new LinearLayoutManager(this));

        // Validação rápida de segurança da sessão
        if (usuarioId == -1) {
            Toast.makeText(this, "Nenhum usuário logado encontrado!", Toast.LENGTH_SHORT).show();
            // Para testes visuais, caso queira testar sem login prévio, comente a linha abaixo:
            // finish();
        }

        // Simulação de lista para teste visual
        List<String> caronasFicticias = new ArrayList<>();
        caronasFicticias.add("Universidade → Ponta Negra");
        caronasFicticias.add("Centro → FUCAPI");

        adapter = new MinhasCaronasAdapter(caronasFicticias);
        recyclerMinhasCaronas.setAdapter(adapter);
    }
}

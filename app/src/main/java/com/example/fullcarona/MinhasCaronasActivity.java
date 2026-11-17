package com.example.fullcarona;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

public class MinhasCaronasActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private SolicitacaoRepository solicitacaoRepository;
    private LinearLayout lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarBarras();

        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) {
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        solicitacaoRepository = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Minhas Caronas", "Acompanhe suas solicitações e caronas.");

        TelaUi.botao(this, root, "Voltar", this::finish);

        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(TelaUi.texto(this, "Suas Solicitações", 20, true));
        root.addView(lista);

        carregarMinhasSolicitacoes();
    }

    private void carregarMinhasSolicitacoes() {
        if (lista == null) return;
        lista.removeAllViews();

        int count = 0;
        for (Solicitacao sol : solicitacaoRepository.listarDoPassageiro(usuarioId)) {
            Carona carona = db.caronaDao().buscarPorId(sol.caronaId);
            if (carona == null) continue;

            Usuario motorista = db.usuarioDao().buscarPorId(carona.motoristaId);
            String nomeMotorista = motorista == null ? "Motorista" : motorista.nome;

            String texto = "Destino: " + carona.destino + "\n" +
                    "Origem: " + carona.origem + "\n" +
                    "Data/Hora: " + DataHoraUtil.exibir(carona.dataHora) + "\n" +
                    "Motorista: " + nomeMotorista + "\n" +
                    "Status: " + sol.status;

            TelaUi.adicionarLinha(this, lista, texto);
            count++;
        }

        if (count == 0) {
            TelaUi.adicionarLinha(this, lista, "Você ainda não solicitou nenhuma carona.");
        }
    }

    private void configurarBarras() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        int navy = Color.parseColor("#041531");
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }
}
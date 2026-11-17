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

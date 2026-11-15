package com.example.fullcarona;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullcarona.CaronasPassageiroAdapter.ItemCarona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePassageiroActivity extends AppCompatActivity {

    static final String STATUS_ABERTA = "ABERTA";
    static final String STATUS_LOTADA = "LOTADA";
    static final String STATUS_PENDENTE = "PENDENTE";
    private static final String KEY_SOLICITADAS = "solicitadas";

    private final Map<Long, String> solicitacoes = new HashMap<>();
    private CaronasPassageiroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarBarras();
        setContentView(R.layout.activity_home_passageiro);

        if (savedInstanceState != null) {
            long[] ids = savedInstanceState.getLongArray(KEY_SOLICITADAS);
            if (ids != null) {
                for (long id : ids) {
                    solicitacoes.put(id, STATUS_PENDENTE);
                }
            }
        }

        List<ItemCarona> caronas = listarAbertas();

        RecyclerView rvCaronas = findViewById(R.id.rvCaronas);
        TextView txtSemCaronas = findViewById(R.id.txtSemCaronas);
        txtSemCaronas.setVisibility(caronas.isEmpty() ? View.VISIBLE : View.GONE);

        adapter = new CaronasPassageiroAdapter(caronas, solicitacoes, this::solicitar);
        rvCaronas.setLayoutManager(new LinearLayoutManager(this));
        rvCaronas.setAdapter(adapter);

        AppCompatButton btnMinhasCaronas = findViewById(R.id.btnMinhasCaronas);
        btnMinhasCaronas.setBackgroundResource(R.drawable.bg_button);
        btnMinhasCaronas.setBackgroundTintList(null);
        btnMinhasCaronas.setAllCaps(false);
        btnMinhasCaronas.setOnClickListener(v ->
                startActivity(new Intent(this, MinhasCaronasActivity.class)));
    }

    // Dados de teste até o banco ficar pronto (trocar por CaronaRepository.listarAbertas())
    private List<ItemCarona> listarAbertas() {
        List<ItemCarona> todas = new ArrayList<>();
        todas.add(new ItemCarona(1, "Centro", "Fucapi", "07:00", 3, STATUS_ABERTA));
        todas.add(new ItemCarona(2, "Cidade Nova", "Fucapi", "07:15", 1, STATUS_ABERTA));
        todas.add(new ItemCarona(3, "Ponta Negra", "Fucapi", "07:30", 0, STATUS_LOTADA));
        todas.add(new ItemCarona(4, "Adrianópolis", "Fucapi", "18:10", 2, STATUS_ABERTA));

        List<ItemCarona> abertas = new ArrayList<>();
        for (ItemCarona carona : todas) {
            if (STATUS_ABERTA.equals(carona.status)) {
                abertas.add(carona);
            }
        }
        return abertas;
    }

    // Trocar por SolicitacaoRepository.jaSolicitou(...) e solicitar(...) quando o banco estiver na main
    private void solicitar(ItemCarona carona, int posicao) {
        if (solicitacoes.containsKey(carona.id)) {
            Toast.makeText(this, R.string.home_ja_solicitou, Toast.LENGTH_SHORT).show();
            return;
        }
        solicitacoes.put(carona.id, STATUS_PENDENTE);
        adapter.notifyItemChanged(posicao);
        Toast.makeText(this, R.string.home_solicitacao_enviada, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        long[] ids = new long[solicitacoes.size()];
        int i = 0;
        for (long id : solicitacoes.keySet()) {
            ids[i++] = id;
        }
        outState.putLongArray(KEY_SOLICITADAS, ids);
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

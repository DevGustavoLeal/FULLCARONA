package com.example.fullcarona;

import android.os.Bundle;
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
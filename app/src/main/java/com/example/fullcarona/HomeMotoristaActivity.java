package com.example.fullcarona;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

import java.util.Calendar;

public class HomeMotoristaActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronas;
    private SolicitacaoRepository solicitacoes;
    private EditText origem, destino, dataHora, vagas, preco;
    private CheckBox cbMoto, cbGratuita;
    private LinearLayout lista;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        db = AppDatabase.getInstance(this);
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Área do motorista", "Publique uma carona e gerencie as solicitações.");

        origem = TelaUi.campo(this, root, "Origem (ex: FUCAPI)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        destino = TelaUi.campo(this, root, "Destino (ex: Armando Mendes)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Campo de Data e Hora Inteligente (Abre Calendário e Relógio ao tocar)
        dataHora = TelaUi.campo(this, root, "Data e Hora (Toque para selecionar)", InputType.TYPE_NULL);
        dataHora.setFocusable(false);
        dataHora.setClickable(true);
        dataHora.setOnClickListener(v -> mostrarSeletorDataHora());

        vagas = TelaUi.campo(this, root, "Número de vagas", InputType.TYPE_CLASS_NUMBER);
        preco = TelaUi.campo(this, root, "Valor da carona (R$)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Opções de Moto e Carona Gratuita
        LinearLayout layoutOpcoes = new LinearLayout(this);
        layoutOpcoes.setOrientation(LinearLayout.HORIZONTAL);
        layoutOpcoes.setGravity(Gravity.CENTER_VERTICAL);

        cbMoto = new CheckBox(this);
        cbMoto.setText(" É de Moto 🏍️");
        cbMoto.setTextColor(Color.rgb(20, 32, 54));
        cbMoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) vagas.setText("1"); // Moto geralmente leva 1 passageiro
        });

        cbGratuita = new CheckBox(this);
        cbGratuita.setText(" Carona Gratuita 🤝");
        cbGratuita.setTextColor(Color.rgb(20, 32, 54));
        cbGratuita.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                preco.setText("0");
                preco.setEnabled(false);
            } else {
                preco.setEnabled(true);
                preco.setText("");
            }
        });

        layoutOpcoes.addView(cbMoto);
        layoutOpcoes.addView(cbGratuita);
        LinearLayout.LayoutParams paramsOpcoes = new LinearLayout.LayoutParams(-1, -2);
        paramsOpcoes.topMargin = TelaUi.dp(this, 10);
        root.addView(layoutOpcoes, paramsOpcoes);

        TelaUi.botao(this, root, "Oferecer carona", this::criarCarona);
        TelaUi.botao(this, root, "Minhas caronas", () -> startActivity(new Intent(this, MinhasCaronasActivity.class)));
        TelaUi.botao(this, root, "Sair", this::sair);

        TextView tituloHistorico = TelaUi.texto(this, "Minhas ofertas e solicitações", 20, true);
        LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(-1, -2);
        paramsTitulo.topMargin = TelaUi.dp(this, 20);
        root.addView(tituloHistorico, paramsTitulo);

        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(lista);

        carregarCaronas();
    }

    private void mostrarSeletorDataHora() {
        Calendar calendario = Calendar.getInstance();
        new DatePickerDialog(this, (view, ano, mes, dia) -> {
            new TimePickerDialog(this, (timeView, hora, minuto) -> {
                String dataFormatada = String.format("%04d-%02d-%02d %02d:%02d", ano, mes + 1, dia, hora, minuto);
                dataHora.setText(dataFormatada);
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show();
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void criarCarona() {
        String horario = DataHoraUtil.normalizar(dataHora.getText().toString());
        int quantidade;
        try {
            quantidade = Integer.parseInt(vagas.getText().toString().trim());
        } catch (NumberFormatException e) {
            quantidade = cbMoto.isChecked() ? 1 : 0;
        }

        if (horario == null) {
            dataHora.setError("Selecione uma data e hora válidas");
            Toast.makeText(this, "Por favor, selecione a data e hora da carona.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantidade <= 0) {
            vagas.setError("Informe o número de vagas");
            Toast.makeText(this, "Informe o número de vagas.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            caronas.criar(new Carona(usuarioId, origem.getText().toString().trim(),
                    destino.getText().toString().trim(), horario, quantidade, "ABERTA"));

            // Limpa os campos após o cadastro
            origem.setText(""); destino.setText(""); dataHora.setText(""); vagas.setText(""); preco.setText("");
            cbMoto.setChecked(false); cbGratuita.setChecked(false); preco.setEnabled(true);

            Toast.makeText(this, "Carona publicada com sucesso!", Toast.LENGTH_SHORT).show();
            carregarCaronas();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Não foi possível salvar a carona.", Toast.LENGTH_LONG).show();
        }
    }

    private void carregarCaronas() {
        if (lista == null) return;
        lista.removeAllViews();
        for (Carona carona : caronas.listarDoMotorista(usuarioId)) {
            TelaUi.adicionarLinha(this, lista, carona.origem + " → " + carona.destino + "\n"
                    + DataHoraUtil.exibir(carona.dataHora) + " · " + carona.vagas + " vaga(s) · " + carona.status);
            if (!"ENCERRADA".equals(carona.status)) {
                TelaUi.botao(this, lista, "Encerrar carona", () -> {
                    caronas.encerrar(carona.id, usuarioId);
                    Toast.makeText(this, "Carona encerrada.", Toast.LENGTH_SHORT).show();
                    carregarCaronas();
                });
            }
            for (Solicitacao pedido : solicitacoes.listarDaCarona(carona.id)) {
                Usuario passageiro = db.usuarioDao().buscarPorId(pedido.passageiroId);
                String nome = passageiro == null ? "Passageiro" : passageiro.nome;
                TelaUi.adicionarLinha(this, lista, nome + " · solicitação " + pedido.status);
                if ("PENDENTE".equals(pedido.status)) {
                    AppCompatButton aceitar = TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacoes.atualizarStatus(pedido.id, "ACEITA"))
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        carregarCaronas();
                    });
                    AppCompatButton recusar = TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacoes.atualizarStatus(pedido.id, "RECUSADA");
                        carregarCaronas();
                    });
                }
            }
        }
    }

    private void sair() {
        new Sessao(this).sair();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
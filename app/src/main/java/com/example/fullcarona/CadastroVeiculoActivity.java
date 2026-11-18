package com.example.fullcarona;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Veiculo;
import com.example.fullcarona.data.VeiculoRepository;

public class CadastroVeiculoActivity extends AppCompatActivity {
    private long usuarioId;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        LinearLayout root = TelaUi.criar(this, "Seu veículo", "Cadastre o veículo usado para oferecer caronas.");
        EditText marca = TelaUi.campo(this, root, "Marca", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText placa = TelaUi.campo(this, root, "Placa (ABC-1234 ou ABC1D23)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        EditText modeloAno = TelaUi.campo(this, root, "Modelo / ano", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        TelaUi.botao(this, root, "Cadastrar veículo", () -> {
            try {
                new VeiculoRepository(AppDatabase.getInstance(this).veiculoDao())
                        .salvar(new Veiculo(usuarioId, marca.getText().toString().trim(),
                                placa.getText().toString().trim(), modeloAno.getText().toString().trim()));
                startActivity(new android.content.Intent(this, HomeMotoristaActivity.class));
                finish();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (RuntimeException e) {
                Toast.makeText(this, "Confira os dados. A placa pode já estar cadastrada.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

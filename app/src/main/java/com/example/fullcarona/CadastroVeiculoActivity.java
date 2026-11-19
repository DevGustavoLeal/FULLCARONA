// Vai em: app/src/main/java/com/example/fullcarona/CadastroVeiculoActivity.java
// (SUBSTITUI o arquivo que já existe na main, feito pelo Pedro com a tela simples)
package com.example.fullcarona;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Veiculo;
import com.example.fullcarona.data.VeiculoRepository;

public class CadastroVeiculoActivity extends AppCompatActivity {

    // Mesma regra do VeiculoRepository: ABC1234, ABC-1234 ou Mercosul ABC1D23
    private static final String REGEX_PLACA = "[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}";

    private long usuarioId;
    private EditText etMarca;
    private EditText etPlaca;
    private EditText etModeloAno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) {   // ninguém logado: volta (igual à versão do Pedro)
            finish();
            return;
        }

        configurarBarras();
        setContentView(R.layout.activity_cadastro_veiculo);

        etMarca = findViewById(R.id.etMarca);
        etPlaca = findViewById(R.id.etPlaca);
        etModeloAno = findViewById(R.id.etModeloAno);

        AppCompatButton btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCadastrar.setBackgroundResource(R.drawable.bg_button);
        btnCadastrar.setBackgroundTintList(null);
        btnCadastrar.setAllCaps(false);
        btnCadastrar.setOnClickListener(v -> cadastrar());

        etModeloAno.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                cadastrar();
                return true;
            }
            return false;
        });
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

    private void cadastrar() {
        String marca = etMarca.getText().toString().trim();
        String placa = etPlaca.getText().toString().trim().toUpperCase();
        String modeloAno = etModeloAno.getText().toString().trim();

        if (TextUtils.isEmpty(marca)) {
            etMarca.requestFocus();
            Toast.makeText(this, R.string.veiculo_erro_marca, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(placa)) {
            etPlaca.requestFocus();
            Toast.makeText(this, R.string.veiculo_erro_placa, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!placa.matches(REGEX_PLACA)) {
            etPlaca.requestFocus();
            Toast.makeText(this, R.string.veiculo_erro_placa_formato, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(modeloAno)) {
            etModeloAno.requestFocus();
            Toast.makeText(this, R.string.veiculo_erro_modelo_ano, Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        try {
            new VeiculoRepository(AppDatabase.getInstance(this).veiculoDao())
                    .salvar(new Veiculo(usuarioId, marca, placa, modeloAno));
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        } catch (RuntimeException e) {
            // placa já cadastrada, ou o usuário já tem veículo
            Toast.makeText(this, R.string.veiculo_erro_salvar, Toast.LENGTH_LONG).show();
            return;
        }

        String resumo = marca + " " + modeloAno + " (" + placa + ")";
        Toast.makeText(this, getString(R.string.veiculo_sucesso, resumo), Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, HomeMotoristaActivity.class));
        finish();
    }
}
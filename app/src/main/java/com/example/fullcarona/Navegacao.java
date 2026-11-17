package com.example.fullcarona;

import android.app.Activity;
import android.content.Intent;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.EnderecoRepository;
import com.example.fullcarona.data.Usuario;
import com.example.fullcarona.data.VeiculoRepository;

final class Navegacao {
    private Navegacao() {}
    static void abrirPosLogin(Activity activity, Usuario usuario) {
        if (usuario == null || usuario.perfil == null) {
            activity.startActivity(new Intent(activity, EscolhaPerfilActivity.class));
            return;
        }
        AppDatabase db = AppDatabase.getInstance(activity);
        if ("MOTORISTA".equals(usuario.perfil)) {
            if (new VeiculoRepository(db.veiculoDao()).buscarPorUsuario(usuario.id) == null)
                activity.startActivity(new Intent(activity, CadastroVeiculoActivity.class));
            else activity.startActivity(new Intent(activity, HomeMotoristaActivity.class));
        } else if ("PASSAGEIRO".equals(usuario.perfil)) {
            if (new EnderecoRepository(db.enderecoDao()).buscarPorUsuario(usuario.id) == null)
                activity.startActivity(new Intent(activity, CadastroEnderecoActivity.class));
            else activity.startActivity(new Intent(activity, HomePassageiroActivity.class));
        } else {
            activity.startActivity(new Intent(activity, EscolhaPerfilActivity.class));
        }
    }
}

package com.example.fullcarona;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda apenas o identificador do usuário autenticado. */
public class Sessao {
    private static final String PREFS = "fullcarona_sessao";
    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final long SEM_USUARIO = -1L;
    private final SharedPreferences preferences;

    public Sessao(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void salvarUsuario(long id) {
        if (id <= 0) throw new IllegalArgumentException("ID de usuário inválido");
        preferences.edit().putLong(KEY_USUARIO_ID, id).apply();
    }

    public long getUsuarioId() {
        return preferences.getLong(KEY_USUARIO_ID, SEM_USUARIO);
    }

    public boolean estaLogado() {
        return getUsuarioId() > 0;
    }

    public void sair() {
        preferences.edit().remove(KEY_USUARIO_ID).apply();
    }
}

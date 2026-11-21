package com.example.fullcarona;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DataHoraUtil {
    private DataHoraUtil() {}
    static String normalizar(String digitada) {
        if (digitada == null || digitada.trim().isEmpty()) return null;
        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        entrada.setLenient(false);
        ParsePosition posicao = new ParsePosition(0);
        Date data = entrada.parse(digitada.trim(), posicao);
        if (data == null || posicao.getIndex() != digitada.trim().length()) return null;
        // Tolerância de 5 minutos no passado para evitar rejeitar horário selecionado durante o uso do picker
        long margemPassado = System.currentTimeMillis() - (5 * 60 * 1000);
        if (data.getTime() < margemPassado) return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).format(data);
    }
    static String exibir(String iso) {
        if (iso == null) return "";
        return iso.replace('T', ' ');
    }
}

package com.example.fullcarona.data;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** PBKDF2-HMAC-SHA256, implementado com APIs presentes desde o minSdk 24. */
final class SenhaUtil {
    private static final int ITERACOES = 120000;
    private static final int TAMANHO_CHAVE = 32;
    private static final int TAMANHO_SALT = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private SenhaUtil() {}

    static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(senha, salt, ITERACOES);
        return ITERACOES + ":" + Base64.encodeToString(salt, Base64.NO_WRAP) + ":"
                + Base64.encodeToString(derived, Base64.NO_WRAP);
    }

    static boolean verificar(String senha, String encoded) {
        if (senha == null || encoded == null) return false;
        try {
            String[] parts = encoded.split(":", 3);
            int rounds = Integer.parseInt(parts[0]);
            if (rounds < 1 || rounds > 1000000) return false;
            byte[] salt = Base64.decode(parts[1], Base64.NO_WRAP);
            byte[] expected = Base64.decode(parts[2], Base64.NO_WRAP);
            byte[] actual = pbkdf2(senha, salt, rounds);
            if (actual.length != expected.length) return false;
            int diff = 0;
            for (int i = 0; i < actual.length; i++) diff |= actual[i] ^ expected[i];
            return diff == 0;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int rounds) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(password.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] blockInput = new byte[salt.length + 4];
            System.arraycopy(salt, 0, blockInput, 0, salt.length);
            blockInput[blockInput.length - 1] = 1;
            byte[] u = mac.doFinal(blockInput);
            byte[] result = u.clone();
            for (int i = 1; i < rounds; i++) {
                u = mac.doFinal(u);
                for (int j = 0; j < result.length; j++) result[j] ^= u[j];
            }
            return java.util.Arrays.copyOf(result, TAMANHO_CHAVE);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível proteger a senha", e);
        }
    }
}

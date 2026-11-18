package com.example.fullcarona.data;

public class UsuarioRepository {
    private final AppDatabase db;
    public UsuarioRepository(AppDatabase db) { this.db = db; }

    public long cadastrar(Usuario usuario) {
        if (usuario == null || vazio(usuario.nome) || vazio(usuario.email) || vazio(usuario.senha) || vazio(usuario.curso))
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios");
        usuario.email = usuario.email.trim().toLowerCase(java.util.Locale.ROOT);
        if (db.usuarioDao().emailExiste(usuario.email)) throw new IllegalArgumentException("Este e-mail já está cadastrado");
        usuario.senhaHash = SenhaUtil.gerarHash(usuario.senha);
        usuario.senha = null;
        return db.usuarioDao().inserir(usuario);
    }
    public boolean emailExiste(String email) { return db.usuarioDao().emailExiste(email.trim()); }
    public Usuario login(String email, String senha) {
        if (email == null || senha == null) return null;
        Usuario usuario = db.usuarioDao().buscarPorEmail(email.trim());
        return usuario != null && SenhaUtil.verificar(senha, usuario.senhaHash) ? usuario : null;
    }
    public Usuario buscarPorId(long id) { return db.usuarioDao().buscarPorId(id); }
    public boolean definirPerfil(long id, String perfil) {
        if (!"MOTORISTA".equals(perfil) && !"PASSAGEIRO".equals(perfil)) return false;
        return db.usuarioDao().definirPerfil(id, perfil) == 1;
    }
    public boolean redefinirSenha(String email, String novaSenha) {
        if (email == null || novaSenha == null || novaSenha.length() < 6) return false;
        return db.usuarioDao().redefinirSenha(email.trim(), SenhaUtil.gerarHash(novaSenha)) == 1;
    }
    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}

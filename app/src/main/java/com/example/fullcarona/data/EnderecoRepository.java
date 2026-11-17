package com.example.fullcarona.data;

public class EnderecoRepository {
    private final EnderecoDao dao;
    public EnderecoRepository(EnderecoDao dao) { this.dao = dao; }
    public long salvar(Endereco endereco) {
        if (endereco == null || endereco.usuarioId <= 0 || vazio(endereco.rua) || vazio(endereco.numero))
            throw new IllegalArgumentException("Rua e número são obrigatórios");
        return dao.inserir(endereco);
    }
    public Endereco buscarPorUsuario(long usuarioId) { return dao.buscarPorUsuario(usuarioId); }
    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}

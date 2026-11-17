package com.example.fullcarona.data;

public class VeiculoRepository {
    private final VeiculoDao dao;
    public VeiculoRepository(VeiculoDao dao) { this.dao = dao; }
    public long salvar(Veiculo veiculo) {
        if (veiculo == null || veiculo.usuarioId <= 0 || vazio(veiculo.marca) || vazio(veiculo.placa) || vazio(veiculo.modeloAno))
            throw new IllegalArgumentException("Preencha os dados do veículo");
        veiculo.placa = veiculo.placa.trim().toUpperCase(java.util.Locale.ROOT);
        if (!veiculo.placa.matches("[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}"))
            throw new IllegalArgumentException("Placa inválida");
        return dao.inserir(veiculo);
    }
    public Veiculo buscarPorUsuario(long usuarioId) { return dao.buscarPorUsuario(usuarioId); }
    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}

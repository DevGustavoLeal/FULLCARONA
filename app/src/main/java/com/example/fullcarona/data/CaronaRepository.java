package com.example.fullcarona.data;

import java.util.List;

public class CaronaRepository {
    private final CaronaDao dao;
    public CaronaRepository(CaronaDao dao) { this.dao = dao; }
    public long criar(Carona carona) {
        if (carona == null || carona.motoristaId <= 0 || vazio(carona.origem) || vazio(carona.destino)
                || vazio(carona.dataHora) || carona.vagas <= 0)
            throw new IllegalArgumentException("Preencha origem, destino, data e vagas maiores que zero");
        carona.status = "ABERTA";
        return dao.inserir(carona);
    }
    public List<Carona> listarAbertas() { return dao.listarAbertas(); }
    public List<Carona> listarDoMotorista(long motoristaId) { return dao.listarDoMotorista(motoristaId); }
    public Carona buscarPorId(long id) { return dao.buscarPorId(id); }
    public boolean diminuirVaga(long caronaId) { return dao.diminuirVaga(caronaId) == 1; }
    public boolean encerrar(long caronaId, long motoristaId) { return dao.encerrar(caronaId, motoristaId) == 1; }
    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}

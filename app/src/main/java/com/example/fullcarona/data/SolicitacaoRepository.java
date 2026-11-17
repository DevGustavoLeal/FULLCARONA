package com.example.fullcarona.data;

import java.util.List;
import java.util.concurrent.Callable;

public class SolicitacaoRepository {
    private final AppDatabase db;
    public SolicitacaoRepository(AppDatabase db) { this.db = db; }

    public boolean solicitar(long caronaId, long passageiroId) {
        Carona carona = db.caronaDao().buscarPorId(caronaId);
        if (carona == null || carona.motoristaId == passageiroId || !"ABERTA".equals(carona.status) || carona.vagas <= 0)
            return false;
        if (jaSolicitou(caronaId, passageiroId)) return false;
        try {
            db.solicitacaoDao().inserir(new Solicitacao(caronaId, passageiroId, "PENDENTE"));
            return true;
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            return false;
        }
    }
    public boolean jaSolicitou(long caronaId, long passageiroId) { return db.solicitacaoDao().jaSolicitou(caronaId, passageiroId); }
    public List<Solicitacao> listarDaCarona(long caronaId) { return db.solicitacaoDao().listarDaCarona(caronaId); }
    public List<Solicitacao> listarDoPassageiro(long passageiroId) { return db.solicitacaoDao().listarDoPassageiro(passageiroId); }

    public boolean atualizarStatus(long id, String status) {
        if (!"ACEITA".equals(status) && !"RECUSADA".equals(status)) return false;
        if ("RECUSADA".equals(status)) return db.solicitacaoDao().atualizarPendente(id, status) == 1;
        try {
            Boolean result = db.runInTransaction((Callable<Boolean>) () -> {
                Solicitacao solicitacao = db.solicitacaoDao().buscarPorId(id);
                if (solicitacao == null || !"PENDENTE".equals(solicitacao.status)) return false;
                if (db.caronaDao().diminuirVaga(solicitacao.caronaId) != 1) return false;
                if (db.solicitacaoDao().atualizarPendente(id, status) != 1)
                    throw new IllegalStateException("A solicitação mudou durante a operação");
                return true;
            });
            return Boolean.TRUE.equals(result);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}

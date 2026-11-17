package com.example.fullcarona.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SolicitacaoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) long inserir(Solicitacao solicitacao);
    @Query("SELECT COUNT(*) > 0 FROM solicitacoes WHERE caronaId = :caronaId AND passageiroId = :passageiroId") boolean jaSolicitou(long caronaId, long passageiroId);
    @Query("SELECT * FROM solicitacoes WHERE caronaId = :caronaId ORDER BY id DESC") List<Solicitacao> listarDaCarona(long caronaId);
    @Query("SELECT * FROM solicitacoes WHERE passageiroId = :passageiroId ORDER BY id DESC") List<Solicitacao> listarDoPassageiro(long passageiroId);
    @Query("SELECT * FROM solicitacoes WHERE id = :id LIMIT 1") Solicitacao buscarPorId(long id);
    @Query("UPDATE solicitacoes SET status = :status WHERE id = :id AND status = 'PENDENTE'") int atualizarPendente(long id, String status);
}

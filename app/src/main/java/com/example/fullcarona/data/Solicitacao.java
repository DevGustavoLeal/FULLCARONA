package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "solicitacoes",
        foreignKeys = {
                @ForeignKey(entity = Carona.class, parentColumns = "id", childColumns = "caronaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "passageiroId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("caronaId"), @Index("passageiroId"), @Index(value = {"caronaId", "passageiroId"}, unique = true)})
public class Solicitacao {
    @PrimaryKey(autoGenerate = true) public long id;
    public long caronaId;
    public long passageiroId;
    /** PENDENTE, ACEITA ou RECUSADA */
    public String status;

    public Solicitacao() {}
    @Ignore
    public Solicitacao(long caronaId, long passageiroId, String status) {
        this.caronaId = caronaId; this.passageiroId = passageiroId; this.status = status;
    }
}

package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "caronas",
        foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "motoristaId", onDelete = ForeignKey.CASCADE),
        indices = {@Index("motoristaId"), @Index("status"), @Index("dataHora")})
public class Carona {
    @PrimaryKey(autoGenerate = true) public long id;
    public long motoristaId;
    public String origem;
    public String destino;
    /** ISO-8601: yyyy-MM-dd'T'HH:mm */
    public String dataHora;
    public int vagas;
    /** ABERTA, LOTADA ou ENCERRADA */
    public String status;

    public Carona() {}
    @Ignore
    public Carona(long motoristaId, String origem, String destino, String dataHora, int vagas, String status) {
        this.motoristaId = motoristaId; this.origem = origem; this.destino = destino;
        this.dataHora = dataHora; this.vagas = vagas; this.status = status;
    }
}

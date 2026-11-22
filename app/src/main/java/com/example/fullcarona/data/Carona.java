package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "caronas",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "motoristaId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("motoristaId")
)
public class Carona {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long motoristaId;
    public String origem;
    public String destino;
    public String dataHora;
    public int vagas;
    public String status;
    public double valor;
    public String modalidade;

    public Carona(long motoristaId, String origem, String destino, String dataHora, int vagas, String status) {
        this(motoristaId, origem, destino, dataHora, vagas, status, 0.0, "Carro");
    }

    public Carona(long motoristaId, String origem, String destino, String dataHora, int vagas, String status, double valor, String modalidade) {
        this.motoristaId = motoristaId;
        this.origem = origem;
        this.destino = destino;
        this.dataHora = dataHora;
        this.vagas = vagas;
        this.status = status;
        this.valor = valor;
        this.modalidade = modalidade;
    }
}
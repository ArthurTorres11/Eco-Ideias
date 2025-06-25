package br.com.ecoideas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "ideias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIdeia status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaImpacto categoriaImpacto;

    // --- CAMPOS DE IMPACTO ---
    private Double valorDoImpacto;

    @Enumerated(EnumType.STRING)
    private UnidadeDeMedida unidadeDeMedida;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataDeCriacao;

    private LocalDateTime dataDeAtualizacao;

    // --- RELACIONAMENTO COM USUÁRIO ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;


    // --- LÓGICA PARA ATUALIZAR DATAS AUTOMATICAMENTE ---
    @PrePersist
    protected void onCreate() {
        this.dataDeCriacao = LocalDateTime.now();
        this.status = StatusIdeia.EM_ANALISE; // Define o status inicial padrão
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataDeAtualizacao = LocalDateTime.now();
    }
}
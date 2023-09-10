package org.acme;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PESSOAS")
@Cacheable
public class Pessoas extends PanacheEntityBase {
    
    @Id
    @Column(name = "ID")
    //@GeneratedValue
    private UUID id;

    @Column(name = "APELIDO")
    private String apelido;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "NASCIMENTO")
    private String nascimento;

    @Column(name = "STACK" )
    @Convert(converter = StringListConverter.class)
    private List<String> stack = Collections.emptyList();

    @Column(name = "BUSCA_TRGM")
    @Generated
    @JsonIgnore
    public String busca;

    public UUID getId() {
        return id;
    }

    public void setId(UUID idUUID) {
        this.id = idUUID;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public List<String> getStack() {
        return stack;
    }

    public void setStack(List<String> stack) {
        if (stack == null) {
            stack = Collections.emptyList();
        }
        this.stack = stack;
    }

 }

package com.juca.projeto_modular.model;

import java.io.Serializable;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@XmlRootElement
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Usuario implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="usuarios",sequenceName="seq_usuarios")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "")
    @Column(name="id", columnDefinition="integer", length=4, nullable=false)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Nome não deve conter números")
    private String nome;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
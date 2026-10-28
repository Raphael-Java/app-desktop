package model.entities;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private String veiculoInteresse;
    private String veiculoTroca;
    private String estagioVenda;
    private String status;

    public Cliente() {
    }

    public Cliente(Integer id, String nome, String documento, String email, String telefone, 
                   String veiculoInteresse, String veiculoTroca, String estagioVenda, String status) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
        this.veiculoInteresse = veiculoInteresse;
        this.veiculoTroca = veiculoTroca;
        this.estagioVenda = estagioVenda;
        this.status = status;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getVeiculoInteresse() { return veiculoInteresse; }
    public void setVeiculoInteresse(String veiculoInteresse) { this.veiculoInteresse = veiculoInteresse; }

    public String getVeiculoTroca() { return veiculoTroca; }
    public void setVeiculoTroca(String veiculoTroca) { this.veiculoTroca = veiculoTroca; }

    public String getEstagioVenda() { return estagioVenda; }
    public void setEstagioVenda(String estagioVenda) { this.estagioVenda = estagioVenda; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

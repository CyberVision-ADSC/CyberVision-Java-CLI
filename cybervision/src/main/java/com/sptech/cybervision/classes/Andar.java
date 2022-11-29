/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.classes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leona
 */
public class Andar {
    private String identificadorAndar;
    private String descricao;
    private Boolean isAtivo;
    private List<Sala> salas;

    public Andar(String identificadorAndar, String descricao, Boolean isAtivo) {
        this.identificadorAndar = identificadorAndar;
        this.descricao = descricao;
        this.isAtivo = isAtivo;
        this.salas = new ArrayList<>();
    }

    public void adicionarSala(Sala sala){
        salas.add(sala);
    
    }

    public String getIdentificadorAndar() {
        return identificadorAndar;
    }

    public void setIdentificadorAndar(String identificadorAndar) {
        this.identificadorAndar = identificadorAndar;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public List<Sala> getSalas() {
        return salas;
    }

    public void setSalas(List<Sala> salas) {
        this.salas = salas;
    }

    @Override
    public String toString() {
        return "Andar = Identificador do Andar: " + identificadorAndar + 
                ", Descricao: " + descricao + ", Ativo: " + isAtivo + ", Salas: " + salas;
                
                
                
    }

   
    
    
    
    
       
}

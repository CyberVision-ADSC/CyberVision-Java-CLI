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
public class Sala {
    private String identificadorSala;
    private String descricao;
    private Boolean isAtivo;
    private List<Computador> computadores;

    public Sala(String identificadorSala, String descricao, Boolean isAtivo) {
        this.identificadorSala = identificadorSala;
        this.descricao = descricao;
        this.isAtivo = isAtivo;
        this.computadores = new ArrayList<>();
    }

    public Sala() {
    }

    public void adicionarComputador(Computador computador){
        computadores.add(computador);
    }

    public String getIdentificadorSala() {
        return identificadorSala;
    }

    public void setIdentificadorSala(String identificadorSala) {
        this.identificadorSala = identificadorSala;
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

    public List<Computador> getComputadores() {
        return computadores;
    }

    public void setComputadores(List<Computador> computadores) {
        this.computadores = computadores;
    }

    @Override
    public String toString() {
        return "\nSala = " + "Identificador da sala: " + identificadorSala + ", Descricao: " + descricao + ", Ativo: " + isAtivo + ", Computadores: " + computadores;
    }

    
    
    
    
    
    
}

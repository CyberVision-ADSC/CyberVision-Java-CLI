/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.classes;

/**
 *
 * @author leona
 */
public class Relatorio {
    private Integer usoCpu;
    private Long usoDisco;
    private Long usoRam;
    private String dataHora;

    public Relatorio(Integer usoCpu, Long usoDisco, Long usoRam, String dataHora) {
        this.usoCpu = usoCpu;
        this.usoDisco = usoDisco;
        this.usoRam = usoRam;
        this.dataHora = dataHora;
    }


    public Integer getUsoCpu() {
        return usoCpu;
    }

    public void setUsoCpu(Integer usoCpu) {
        this.usoCpu = usoCpu;
    }

    public Long getUsoDisco() {
        return usoDisco;
    }

    public void setUsoDisco(Long usoDisco) {
        this.usoDisco = usoDisco;
    }

    public Long getUsoRam() {
        return usoRam;
    }

    public void setUsoRam(Long usoRam) {
        this.usoRam = usoRam;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "\nRelatorio = " + "Uso cpu: " + usoCpu + ", Uso disco: " + usoDisco + ", Uso ram: " + usoRam + ", DataHora: " + dataHora;
    }

   
    
    
    
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.classes;

/**
 *
 * @author leona
 */
public class Processo {
    private Integer pid;
    private String nomeProcesso;
    private Double usoCpu;
    private Double usoMemoria;

    public Processo(Integer pid, String nomeProcesso, Double usoCpu, Double usoMemoria) {
        this.pid = pid;
        this.nomeProcesso = nomeProcesso;
        this.usoCpu = usoCpu;
        this.usoMemoria = usoMemoria;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getNomeProcesso() {
        return nomeProcesso;
    }

    public void setNomeProcesso(String nomeProcesso) {
        this.nomeProcesso = nomeProcesso;
    }

    public Double getUsoCpu() {
        return usoCpu;
    }

    public void setUsoCpu(Double usoCpu) {
        this.usoCpu = usoCpu;
    }

    public Double getUsoMemoria() {
        return usoMemoria;
    }

    public void setUsoMemoria(Double usoMemoria) {
        this.usoMemoria = usoMemoria;
    }

    @Override
    public String toString() {
        return "Processo = " + "PID: " + pid + ", Nome do Processo: " + nomeProcesso + ", Uso cpu: " + usoCpu + ", Uso memoria: " + usoMemoria;
    }
    
    
    
}

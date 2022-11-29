/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.classes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import com.jezhumble.javasysmon.JavaSysMon;
import com.sptech.cybervision.conexoes.ConexaoAws;
import com.sptech.cybervision.conexoes.ConexaoAzure;
import com.sptech.cybervision.conexoes.ConexaoLocal;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logs.Logs;
import org.json.JSONObject;

/**
 *
 * @author leona
 */
public class Computador {

    private String hostname;
    private String processador;
    private Integer arquitetura;
    private String fabricante;
    private Long ram;
    private Long disco;
    private String sistemaOperacional;
    private Boolean problemaCpu;
    private Boolean problemaDisco;
    private Boolean problemaMemoria;
    private Boolean problemaFisico;
    private Boolean isAtivo;
    private List<Relatorio> relatorios;
    private List<Processo> processos;

    public Computador(String hostname, String processador, Integer arquitetura, String fabricante, Long ram, Long disco, String sistemaOperacional, Boolean problemaCpu, Boolean problemaDisco, Boolean problemaMemoria, Boolean problemaFisico, Boolean isAtivo) {
        this.hostname = hostname;
        this.processador = processador;
        this.arquitetura = arquitetura;
        this.fabricante = fabricante;
        this.ram = ram;
        this.disco = disco;
        this.sistemaOperacional = sistemaOperacional;
        this.problemaCpu = problemaCpu;
        this.problemaDisco = problemaDisco;
        this.problemaMemoria = problemaMemoria;
        this.problemaFisico = problemaFisico;
        this.isAtivo = isAtivo;
        this.relatorios = new ArrayList<>();
        this.processos = new ArrayList<>();
    }

    public Computador() {
    }

    ConexaoAzure conexaoAzure = new ConexaoAzure();
    ConexaoLocal conexaoLocal = new ConexaoLocal();
    ConexaoAws conexaoAws = new ConexaoAws();
    Usuario usuario = new Usuario();
    Looca looca = new Looca();
    Logs logs = new Logs();
    JSONObject json = new JSONObject();
    SystemTray tray = SystemTray.getSystemTray();

    private Integer contadorRelatorios = 10;
    private static final JavaSysMon SYS_MON = new JavaSysMon();

    public void coletarRelatoriosProcessos(Integer fkComputador, Integer fkSala, String hostName, Integer fkComputadorLocal) {

        setHostname(hostName);
       
        //Temporizador que é executado a cada 5 segundos coletando relatórios e processos
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Long converteGiga = 1073741824L; // Converte para Giga os valores em bytes

                Long totalDisco = 0L;
                Long totalDiscoDisponivel = 0L;

                for (Volume volume : looca.getGrupoDeDiscos().getVolumes()) {
                    totalDisco += volume.getTotal();
                }

                for (Volume volume : looca.getGrupoDeDiscos().getVolumes()) {
                    totalDiscoDisponivel += volume.getDisponivel();
                }

                // Regra de três e conversões para passar os valores de consumo para porcentagem
                Long totalDiscoGiga = totalDisco / converteGiga;
                Long totalDiscoDisponivelGiga = totalDiscoDisponivel / converteGiga;
                Long multiplicacaoDiscoDisponivelX100 = totalDiscoDisponivelGiga * 100;
                Long divisaoMultiporDiscoGiga = multiplicacaoDiscoDisponivelX100 / totalDiscoGiga;
                Long totalRam = looca.getMemoria().getTotal() / 1048576L;
                Long usoRamTotal = looca.getMemoria().getEmUso() / 1048576L;
                Long multiplicacaoUsoXCem = usoRamTotal * 100;

                // Uso de cada componente do hardware convertido em porcentagem.
                Integer usoCpu = looca.getProcessador().getUso().intValue();
                Long usoDisco = 100 - divisaoMultiporDiscoGiga;
                Long usoRam = multiplicacaoUsoXCem / totalRam;

                // Pegando data e hora do momento que o relatório é gerado
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String dataHora = dtf.format(LocalDateTime.now());

                // Inserindo relatórios na tabela
                
                conexaoAws.getConnection().update(
                        "INSERT INTO relatorio (uso_cpu, uso_disco, uso_ram, data_hora, fk_computador,"
                        + " fk_sala) VALUES (?, ?, ?, ?, ?, ?)",
                        usoCpu, usoDisco, usoRam, dataHora, fkComputador, fkSala);

                conexaoAzure.getConnection().update(
                        "INSERT INTO relatorio (uso_cpu, uso_disco, uso_ram, data_hora, fk_computador,"
                        + " fk_sala) VALUES (?, ?, ?, ?, ?, ?)",
                        usoCpu, usoDisco, usoRam, dataHora, fkComputador, fkSala);

                conexaoLocal.getConnection().update(
                        "INSERT INTO relatorio (uso_cpu, uso_disco, uso_ram, data_hora, fk_computador) VALUES (?, ?, ?, ?, ?)",
                        usoCpu, usoDisco, usoRam, dataHora, fkComputadorLocal);
                // Instânciando cada relatório gerado
                ;

                // Variáveis que indicam que se o componente está com problema ou não sendo criadas
                Boolean problemaCpuRelatorio = false;
                Boolean problemaDiscoRelatorio = false;
                Boolean problemaMemoriaRelatorio = false;

                DateTimeFormatter dtft = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                String dataHoraText = dtft.format(LocalDateTime.now());

                // Se o DISCO estiver com mais de 90% sendo usado é gerado o alerta
                if (usoDisco >= 90) {
                    problemaDiscoRelatorio = true;

                    String caminhoLocalHome = new Computador().criarPastaLog();

                    if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                        logs.logAlerta(String.format("%s\\logs\\%s-Log-alertas", caminhoLocalHome, dataHora),
                                "\n A máquina ", hostName, " esta com o disco com uso em nível critico de ",
                                usoDisco.toString(), "% ás ", dataHoraText);

                    } else {

                        logs.logAlerta(String.format("%s/logs/%s-Log-alertas", caminhoLocalHome, dataHora),
                                "\n A máquina ", hostName, " esta com o disco com uso em nível critico de ",
                                usoDisco.toString(), "% ás ", dataHoraText);
                    }

                    json.put("text", ":rotating_light: ALERTA :rotating_light:\n"
                            + " O DISCO da máquina com o hostname " + hostName + " está utilizando " + usoDisco + "% da capacidade!");

                    try {
                        Slack.enviarMensagem(json);
                    } catch (IOException ex) {
                        Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Se a RAM estiver com mais de 90% sendo usado é gerado o alerta
                if (usoRam >= 90) {
                    problemaMemoriaRelatorio = true;

                    String caminhoLocalHome = new Computador().criarPastaLog();

                    if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                        logs.logAlerta(String.format("%s\\logs\\%s-Log-alertas", caminhoLocalHome, dataHora),
                                "\n A máquina ", hostName, " esta com a ram com uso em nível critico de ",
                                usoRam.toString(), "% ás ", dataHoraText);

                    } else {

                        logs.logAlerta(String.format("%s/logs/%s-Log-alertas", caminhoLocalHome, dataHora),
                                "\n A máquina ", hostName, " esta com a ram com uso em nível critico de ",
                                usoRam.toString(), "% ás ", dataHoraText);
                    }
                    json.put("text", ":rotating_light: ALERTA :rotating_light:\n"
                            + " A MEMÓRIA RAM da máquina com o hostname " + hostName + " está utilizando " + usoRam + "% da capacidade!");
                    try {
                        Slack.enviarMensagem(json);
                    } catch (IOException ex) {
                        Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Se a CPU estiver com mais de 80% sendo usado em 10 relatórios seguidos 
                // é gerado o alerta
                if (usoCpu >= 80) {
                    contadorRelatorios--;

                    if (contadorRelatorios <= 0) {
                        problemaCpuRelatorio = true;

                        String caminhoLocalHome = new Computador().criarPastaLog();

                        if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                            logs.logAlerta(String.format("%s\\logs\\%s-Log-alertas", caminhoLocalHome, dataHora),
                                    "\n A máquina ", hostName, " esta com a cpu com uso em nível critico de ",
                                    usoCpu.toString(), "% ás", dataHoraText);

                        } else {

                            logs.logAlerta(String.format("%s/logs/%s-Log-alertas", caminhoLocalHome, dataHora),
                                    "\n A máquina ", hostName, " esta com a cpu com uso em nível critico de ",
                                    usoCpu.toString(), "% ás", dataHoraText);
                        }
                        json.put("text", ":rotating_light: ALERTA :rotating_light:\n"
                                + " A CPU da máquina com o hostname " + hostName + " está utilizando " + usoCpu + "% da capacidade!");
                        try {
                            Slack.enviarMensagem(json);
                        } catch (IOException ex) {
                            Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {
                    contadorRelatorios = 10;
                }

                conexaoAws.getConnection().update(
                        "UPDATE computador SET problema_cpu = ?, problema_disco = ?, "
                        + "problema_memoria = ? "
                        + "WHERE id_computador = ?", problemaCpuRelatorio,
                        problemaDiscoRelatorio, problemaMemoriaRelatorio, fkComputador);

                conexaoAzure.getConnection().update(
                        "UPDATE computador SET problema_cpu = ?, problema_disco = ?, "
                        + "problema_memoria = ? "
                        + "WHERE id_computador = ?", problemaCpuRelatorio,
                        problemaDiscoRelatorio, problemaMemoriaRelatorio, fkComputador);

                conexaoLocal.getConnection().update(
                        "UPDATE computador SET problema_cpu = ?, problema_disco = ?, "
                        + "problema_memoria = ? "
                        + "WHERE id_computador = ?", problemaCpuRelatorio,
                        problemaDiscoRelatorio, problemaMemoriaRelatorio, fkComputadorLocal);

            }
        },
                0, 5000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                for (com.github.britooo.looca.api.group.processos.Processo processo : looca.getGrupoDeProcessos().getProcessos()) {
                    Integer pidProcesso = processo.getPid();
                    String nomeProcesso = processo.getNome();

                    BigDecimal usoCpuBig = BigDecimal.valueOf(processo.getUsoCpu());
                    BigDecimal usoMemoriaBig = BigDecimal.valueOf(processo.getUsoMemoria());

                    usoCpuBig = usoCpuBig.setScale(2, RoundingMode.HALF_EVEN);
                    usoMemoriaBig = usoMemoriaBig.setScale(2, RoundingMode.HALF_EVEN);

                    Double usoCpuProcesso = usoCpuBig.doubleValue();
                    Double usoMemoriaProcesso = usoMemoriaBig.doubleValue();

                    // Validando se o processo existe ou não na tabela pelo pid e fk do computador
                    List<Map<String, Object>> registroProcesso = conexaoAws.getConnection().queryForList("select * from processo where pid = ? and fk_computador = ?", pidProcesso, fkComputador);

                    if (usoCpuProcesso > 1 || usoMemoriaProcesso > 1) {
                        if (registroProcesso.isEmpty()) {

                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                            String dataHoraProcesso = dateFormat.format(LocalDateTime.now());

                            conexaoAws.getConnection().update(
                                    "INSERT INTO processo (pid, nome, uso_cpu, uso_memoria, data_hora_atualizado, fk_computador)"
                                    + " VALUES (?, ?, ?, ?, ?, ?)",
                                    pidProcesso, nomeProcesso, usoCpuProcesso, usoMemoriaProcesso, dataHoraProcesso, fkComputador);

                            conexaoAzure.getConnection().update(
                                    "INSERT INTO processo (pid, nome, uso_cpu, uso_memoria, data_hora_atualizado, fk_computador)"
                                    + " VALUES (?, ?, ?, ?, ?, ?)",
                                    pidProcesso, nomeProcesso, usoCpuProcesso, usoMemoriaProcesso, dataHoraProcesso, fkComputador);

                            conexaoLocal.getConnection().update(
                                    "INSERT INTO processo (pid, nome, uso_cpu, uso_memoria, data_hora_atualizado, fk_computador)"
                                    + " VALUES (?, ?, ?, ?, ?, ?)",
                                    pidProcesso, nomeProcesso, usoCpuProcesso, usoMemoriaProcesso, dataHoraProcesso, fkComputadorLocal);

                            Processo process = new Processo(pidProcesso, nomeProcesso,
                                    usoCpuProcesso, usoMemoriaProcesso);

                        } else {
                            // Se o processo existir na tabela ele é apenas atualizado com dados atuais
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                            String dataHoraProcessoAtualizado = dateFormat.format(LocalDateTime.now());

                            conexaoAws.getConnection().update(
                                    "UPDATE processo SET uso_cpu = ?, uso_memoria = ?, data_hora_atualizado = ? WHERE pid = ?",
                                    usoCpuProcesso, usoMemoriaProcesso, dataHoraProcessoAtualizado, pidProcesso);

                            conexaoAzure.getConnection().update(
                                    "UPDATE processo SET uso_cpu = ?, uso_memoria = ?, data_hora_atualizado = ? WHERE pid = ?",
                                    usoCpuProcesso, usoMemoriaProcesso, dataHoraProcessoAtualizado, pidProcesso);

                            conexaoLocal.getConnection().update(
                                    "UPDATE processo SET uso_cpu = ?, uso_memoria = ?, data_hora_atualizado = ? WHERE pid = ?",
                                    usoCpuProcesso, usoMemoriaProcesso, dataHoraProcessoAtualizado, pidProcesso);
                        }
                    }
                }

            }

        }, 0, 5000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<Map<String, Object>> registroProcessoMatar = conexaoAws.getConnection().queryForList("select * from processo_matar where is_executado = ? and fk_computador = ?", false, fkComputador);
                if (registroProcessoMatar != null && !registroProcessoMatar.isEmpty()) {
                    try {

                        
                        Integer pidMatar = Integer.parseInt(registroProcessoMatar.get(0).get("pid_processo").toString());

//                        Runtime.getRuntime().exec("taskkill /F /PID 827");
                        SYS_MON.killProcess(pidMatar);
                        SYS_MON.killProcessTree(pidMatar, true);
                        System.out.println("Processo morto!");
                        DateTimeFormatter dhm = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        String dataHoraMorte = dhm.format(LocalDateTime.now());

                        conexaoAws.getConnection().update("UPDATE processo_matar SET is_executado = ?, data_hora_executado = ?", true, dataHoraMorte);

                        conexaoAzure.getConnection().update("UPDATE processo_matar SET is_executado = ?, data_hora_executado = ?", true, dataHoraMorte);

                        conexaoLocal.getConnection().update("UPDATE processo_matar SET is_executado = ?, data_hora_executado = ?", true, dataHoraMorte);
                    } catch (Exception e) {
                        System.out.println("OCORREU UM ERRO AO FINALIZAR O PROCESSO" + e);
                    }

                }

            }
        }, 0, 2000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<Map<String, Object>> registroNotificar = conexaoAws.getConnection().queryForList("select * from notificar_aluno where is_executado = ? and fk_computador = ?", false, fkComputador);
                if (registroNotificar != null && !registroNotificar.isEmpty()) {

                    SystemTray tray = SystemTray.getSystemTray();
                    Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                    TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
                    trayIcon.setImageAutoSize(true);
                    trayIcon.setToolTip("System tray icon demo");
                    try {
                        tray.add(trayIcon);
                    } catch (AWTException ex) {
                        Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    DateTimeFormatter dhn = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    String dataHoraNotificacao = dhn.format(LocalDateTime.now());

                    trayIcon.displayMessage("ATENÇÂO", "Detectamos muitos processos"
                            + " sendo executados ao mesmo tempo nessa máquina,"
                            + " verifique se está utilizando todos os aplicativos abertos!", TrayIcon.MessageType.WARNING);

                    conexaoAws.getConnection().update("UPDATE notificar_aluno SET is_executado = ?, data_hora_executado = ?", true, dataHoraNotificacao);
                    conexaoAzure.getConnection().update("UPDATE notificar_aluno SET is_executado = ?, data_hora_executado = ?", true, dataHoraNotificacao);
                    conexaoLocal.getConnection().update("UPDATE notificar_aluno SET is_executado = ?, data_hora_executado = ?", true, dataHoraNotificacao);
                }

            }

        }, 0, 2000);

    }

    private String retorneComando(String comando) throws IOException {

        final ArrayList<String> comandos = new ArrayList<String>();

        Looca looca = new Looca();

        if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

            comandos.add("cmd");
            comandos.add("/c");
            comandos.add(comando);
        } else {

            comandos.add("/bin/bash");
            comandos.add("-c");
            comandos.add(comando);
        }

        BufferedReader br = null;
        String retorno = "";
        try {
            final ProcessBuilder p = new ProcessBuilder(comandos);
            final Process process = p.start();
            final InputStream is = process.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                retorno = line;
            }

        } catch (IOException ioe) {
            System.out.println("Erro ao executar comando" + ioe.getMessage());
        } finally {
            secureClose(br);
            return retorno;
        }

    }

    private void secureClose(final Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (IOException ex) {
            System.out.println("Erro = " + ex.getMessage());
        }
    }

    public String buscarCaminhoLocal() throws IOException {

        Looca looca = new Looca();

        String caminhoLocal;
        if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {
            caminhoLocal = this.retorneComando("echo %homedrive%%homepath%");

        } else {
            this.retorneComando("chmod 777 $HOME");
            caminhoLocal = this.retorneComando("echo $HOME");

        }
        return caminhoLocal;
    }

    public String criarPastaLog() {

        String caminhoLocalHome = "";

        try {
            String FileSystem = "";
            if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {
                FileSystem = "\\";
            } else {
                FileSystem = "/";
            }

            caminhoLocalHome = new Computador().buscarCaminhoLocal();

            File arquivo = new File(String.format("%s%slogs", caminhoLocalHome, FileSystem));

            arquivo.mkdir();

        } catch (IOException ex) {
            System.out.println("Erro ao criar arquivo para os logs");
        }

        return caminhoLocalHome;
    }


    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getProcessador() {
        return processador;
    }

    public void setProcessador(String processador) {
        this.processador = processador;
    }

    public Integer getArquitetura() {
        return arquitetura;
    }

    public void setArquitetura(Integer arquitetura) {
        this.arquitetura = arquitetura;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public Long getRam() {
        return ram;
    }

    public void setRam(Long ram) {
        this.ram = ram;
    }

    public Long getDisco() {
        return disco;
    }

    public void setDisco(Long disco) {
        this.disco = disco;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public Boolean getProblemaCpu() {
        return problemaCpu;
    }

    public void setProblemaCpu(Boolean problemaCpu) {
        this.problemaCpu = problemaCpu;
    }

    public Boolean getProblemaDisco() {
        return problemaDisco;
    }

    public void setProblemaDisco(Boolean problemaDisco) {
        this.problemaDisco = problemaDisco;
    }

    public Boolean getProblemaMemoria() {
        return problemaMemoria;
    }

    public void setProblemaMemoria(Boolean problemaMemoria) {
        this.problemaMemoria = problemaMemoria;
    }

    public Boolean getProblemaFisico() {
        return problemaFisico;
    }

    public void setProblemaFisico(Boolean problemaFisico) {
        this.problemaFisico = problemaFisico;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        this.isAtivo = ativo;
    }

    public List<Relatorio> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(List<Relatorio> relatorios) {
        this.relatorios = relatorios;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }

    public Integer getContadorRelatorios() {
        return contadorRelatorios;
    }

    public void setContadorRelatorios(Integer contadorRelatorios) {
        this.contadorRelatorios = contadorRelatorios;
    }

    @Override
    public String toString() {
        return "\nComputador:"
                + "\nHostname :" + hostname
                + "\nProcessador: " + processador
                + "\nArquitetura: " + arquitetura
                + "\nFabricante: " + fabricante
                + "\nMemoria Ram: " + ram
                + "\nDisco: " + disco
                + "\nSistema operacional: " + sistemaOperacional
                + "\nAtivo: " + isAtivo
                + "\nRelatorios: " + relatorios
                + "\nProcessos: " + processos;

    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision;

import com.github.britooo.looca.api.core.Looca;
import com.sptech.cybervision.classes.Andar;
import com.sptech.cybervision.classes.Computador;
import com.sptech.cybervision.classes.Faculdade;
import com.sptech.cybervision.classes.Sala;
import com.sptech.cybervision.classes.Slack;
import com.sptech.cybervision.classes.Usuario;
import com.sptech.cybervision.conexoes.ConexaoAws;
import com.sptech.cybervision.conexoes.ConexaoAzure;
import com.sptech.cybervision.conexoes.ConexaoLocal;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import logs.Logs;
import org.json.JSONObject;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 *
 * @author henri
 */
public class CLI {

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Scanner leitor2 = new Scanner(System.in);
        ConexaoAws conexaoAws = new ConexaoAws();
        ConexaoAzure conexaoAzure = new ConexaoAzure();
        ConexaoLocal conexaoLocal = new ConexaoLocal();
        Logs logs = new Logs();
        Looca looca = new Looca();
        Faculdade faculdade = new Faculdade();
        Usuario usuario = new Usuario();
        JSONObject json = new JSONObject();
        String hostNameAssociar;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String dataHora = dtf.format(LocalDateTime.now());
        DateTimeFormatter dtft = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dataHoraTexto = dtft.format(LocalDateTime.now());
        String nomeUsuario = "";

        System.out.println(" __       __   ___  __          __     __       \n"
                + "/  ` \\ / |__) |__  |__) \\  / | /__` | /  \\ |\\ | \n"
                + "\\__,  |  |__) |___ |  \\  \\/  | .__/ | \\__/ | \\|");

        System.out.println("Bem vindo, para iniciarmos faça o seu login: ");

        Boolean error = false;
        System.out.println("Para iniciarmos faça o seu login: ");

////////LOGIN 
        do {
            System.out.println("Digite seu e-mail:");
            String emailDigitado = leitor.nextLine();
            System.out.println("Digite sua senha:");
            String senhaDigitada = leitor.nextLine();
            error = false;
            try {
                Map<String, Object> registro = conexaoAws.getConnection().queryForMap("select * from usuario where email = ? and senha = ?", emailDigitado, senhaDigitada);

                // Instânciando usuário que logou
                List<Map<String, Object>> listaUsuario = conexaoAws.getConnection().queryForList("select * from usuario where email = ?", emailDigitado);
                nomeUsuario = listaUsuario.get(0).get("nome").toString();
                Integer fkFaculdade = Integer.parseInt(listaUsuario.get(0).get("fk_faculdade").toString());

                String nomeUser = nomeUsuario;
                String caminhoLocalHome = new Computador().criarPastaLog();

                if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                    logs.logConexao(String.format("%s\\logs\\%s-Log-Conexão-Login", caminhoLocalHome, dataHora), nomeUser, " Logou na aplicação ás ", dataHoraTexto);
                } else {

                    logs.logConexao(String.format("%s/logs/%s-Log-Conexão-Login", caminhoLocalHome, dataHora), nomeUser, " Logou na aplicação ás ", dataHoraTexto);
                }

                // Instânciando faculdade que o usuário pertence
            } catch (EmptyResultDataAccessException e) {
                System.out.println("Email ou senha incorretos!");
                System.out.println("*'enter' para continuar*");
                leitor.nextLine();
                error = true;

                String caminhoLocalHome = new Computador().criarPastaLog();

                if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                    logs.logErro(String.format("%s\\logs\\%s-Log-Erro-Login", caminhoLocalHome, dataHora), " Erro ao logar ás ", dataHoraTexto);
                } else {

                    logs.logErro(String.format("%s/logs/%s-Log-Erro-Login", caminhoLocalHome, dataHora), " Erro ao logar ás ", dataHoraTexto);
                }

            }
        } while (error);
        System.out.println("Bem vindo(a) " + nomeUsuario);

////////ASSOCIAR MÁQUINA
        error = false;
        do {
            System.out.println("Associar hostname:");
            hostNameAssociar = leitor.nextLine();

            try {
                conexaoAws.getConnection().queryForMap(
                        "select * from computador WHERE hostname = ?", hostNameAssociar);

                List<Map<String, Object>> listaHostname = conexaoLocal.getConnection().queryForList("select * from computador where hostname = ?", hostNameAssociar);

                if (listaHostname.isEmpty()) {
                    conexaoLocal.getConnection().update("INSERT INTO computador (id_computador, hostname) VALUES (?, ?)", 1, hostNameAssociar);
                }

                Integer fkComputadorLocal = 1;

                String caminhoLocalHome = new Computador().criarPastaLog();

                if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                    logs.logConexao(String.format("%s\\logs\\%s-Log-Conexao-Maquina", caminhoLocalHome, dataHora), hostNameAssociar, " Foi associada ás ", dataHoraTexto);

                } else {

                    logs.logConexao(String.format("%s/logs/%s-Log-Conexao-Maquina", caminhoLocalHome, dataHora), hostNameAssociar, " Foi associada ás ", dataHoraTexto);

                }

                // Pegando fk_sala do computador cujo hostname foi inserido
                List<Map<String, Object>> listaComputador = conexaoAws.getConnection().queryForList("select * from computador where hostname = ?", hostNameAssociar);
                Boolean isAtivoComputador = Boolean.parseBoolean(listaComputador.get(0).get("is_ativo").toString());
                Integer fkSala = Integer.parseInt(listaComputador.get(0).get("fk_sala").toString());
                Integer fkComputador = Integer.parseInt(listaComputador.get(0).get("id_computador").toString());

                usuario.associarMaquina(hostNameAssociar, isAtivoComputador, fkComputador, fkSala, fkComputadorLocal);

            } catch (EmptyResultDataAccessException e) {
                System.out.println("Hostname não encontrado!");
                System.out.println("*'enter' para continuar*");
                leitor.nextLine();
                error = true;

                String caminhoLocalHome = new Computador().criarPastaLog();

                if (looca.getSistema().getSistemaOperacional().equalsIgnoreCase("Windows")) {

                    logs.logErro(String.format("%s\\logs\\%s-Log-erros-maquina", caminhoLocalHome, dataHora), " Erro ao associar ás ", dataHoraTexto);

                } else {

                    logs.logErro(String.format("%s/logs/%s-Log-erros-maquina", caminhoLocalHome, dataHora), " Erro ao associar ás ", dataHoraTexto);
                }
            }
        } while (error);

        //MENU CHAMADOS
        Integer escolha;
        String raAluno;
        String hostNameChamado;
        List<Map<String, Object>> registroMaquina;
        String descricao;

        do {

            System.out.println("Para abrir um chamado digite 1");
            

            escolha = leitor.nextInt();

            if (escolha == 1) {

                System.out.println("Bem vindo á página de chamados!");

                do {
                    System.out.println("Digite seu RA:");
                    raAluno = leitor2.nextLine();

                } while (raAluno.isEmpty());

                do {
                    System.out.println("Digite seu Hostname:");
                    hostNameChamado = leitor2.nextLine();

                    registroMaquina = conexaoAws.getConnection().queryForList("select * from computador where hostname = ?", hostNameChamado);

                } while (registroMaquina.isEmpty());

                do {
                    System.out.println("Digite a descricao do chamado:");
                    descricao = leitor2.nextLine();

                } while (descricao.isEmpty());

                Integer fkComputador = Integer.parseInt(registroMaquina.get(0).get("id_computador").toString());
                List<Map<String, Object>> registroHostLocal = conexaoLocal.getConnection().queryForList("select * from computador where hostname = ?", hostNameChamado);
                Integer fkComputadorLocal = Integer.parseInt(registroHostLocal.get(0).get("id_computador").toString());
                String status = "Pendente";
                DateTimeFormatter data_hora = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String dataHoraCriacao = data_hora.format(LocalDateTime.now());
                conexaoAws.getConnection().update(
                        "INSERT INTO chamados (ra_aluno, hostname, descricao_ocorrido,"
                        + " status_chamado, data_hora_criacao, fk_computador) VALUES (?, ?, ?, ?, ?, ?)",
                        raAluno, hostNameChamado, descricao, status, dataHoraCriacao, fkComputador);

                conexaoAws.getConnection().update(
                        "UPDATE computador SET problema_fisico = ? WHERE hostname = ?",
                        true, hostNameChamado);

                conexaoAzure.getConnection().update(
                        "INSERT INTO chamados (ra_aluno, hostname, descricao_ocorrido,"
                        + " status_chamado, data_hora_criacao, fk_computador) VALUES (?, ?, ?, ?, ?, ?)",
                        raAluno, hostNameChamado, descricao, status, dataHoraCriacao, fkComputador);

                conexaoAzure.getConnection().update(
                        "UPDATE computador SET problema_fisico = ? WHERE hostname = ?",
                        true, hostNameChamado);

                conexaoLocal.getConnection().update(
                        "INSERT INTO chamados (ra_aluno, hostname, descricao_ocorrido,"
                        + " status_chamado, data_hora_criacao, fk_computador) VALUES (?, ?, ?, ?, ?, ?)",
                        raAluno, hostNameChamado, descricao, status, dataHoraCriacao, fkComputadorLocal);

                conexaoLocal.getConnection().update(
                        "UPDATE computador SET problema_fisico = ? WHERE hostname = ?",
                        true, hostNameChamado);

                System.out.println("Chamado enviado com sucesso!");

                json.put("text", ":rotating_light: ALERTA :rotating_light:\n"
                        + "Um chamado acaba de ser aberto referente a máquina com o hostname: " + hostNameChamado);
                try {
                    Slack.enviarMensagem(json);
                } catch (IOException ex) {
                    Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Computador.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Comando inválido!");

            }

        } while (true);
    }
}

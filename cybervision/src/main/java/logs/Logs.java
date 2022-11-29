/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author leona
 */
public class Logs {

    public void logConexao(String caminhoDiretorio, String usuario, String texto, String dataHora) {

        try (
                FileWriter criadorDeArquvios = new FileWriter(caminhoDiretorio, true);
                BufferedWriter buffer = new BufferedWriter(criadorDeArquvios);
                PrintWriter escritorDeArquivos = new PrintWriter(buffer);) {
            escritorDeArquivos.append(usuario);
            escritorDeArquivos.append(texto);
            escritorDeArquivos.append(dataHora);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void logConexao(String caminhoDiretorio, String quebraLinha, String usuario, String texto, String dataHora) {

        try (
                FileWriter criadorDeArquvios = new FileWriter(caminhoDiretorio, true);
                BufferedWriter buffer = new BufferedWriter(criadorDeArquvios);
                PrintWriter escritorDeArquivos = new PrintWriter(buffer);) {
            escritorDeArquivos.append(quebraLinha);
            escritorDeArquivos.append(usuario);
            escritorDeArquivos.append(texto);
            escritorDeArquivos.append(dataHora);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void logAlerta(String caminhoDiretorio, String quebraLinha, String usuario, String texto, String valor, String texto2, String dataHora) {

        try (
                FileWriter criadorDeArquvios = new FileWriter(caminhoDiretorio, true);
                BufferedWriter buffer = new BufferedWriter(criadorDeArquvios);
                PrintWriter escritorDeArquivos = new PrintWriter(buffer);) {
            escritorDeArquivos.append(quebraLinha);
            escritorDeArquivos.append(usuario);
            escritorDeArquivos.append(texto);
            escritorDeArquivos.append(valor);
            escritorDeArquivos.append(texto2);
            escritorDeArquivos.append(dataHora);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void logErro(String caminhoDiretorio, String texto, String dataHora) {

        try (
                FileWriter criadorDeArquvios = new FileWriter(caminhoDiretorio, true);
                BufferedWriter buffer = new BufferedWriter(criadorDeArquvios);
                PrintWriter escritorDeArquivos = new PrintWriter(buffer);) {

            escritorDeArquivos.append(texto);
            escritorDeArquivos.append(dataHora);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}

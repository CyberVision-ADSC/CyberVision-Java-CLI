/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.conexoes;

import com.sptech.cybervision.classes.Slack;
import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author Gabriel
 */
public class App {
    public static void main(String[] args)throws IOException,InterruptedException {
        JSONObject json =  new JSONObject();
        
        json.put("text", "Olá Mundo! :hankey: ");
        Slack.enviarMensagem(json);
    }
}

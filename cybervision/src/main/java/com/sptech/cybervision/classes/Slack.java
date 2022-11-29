/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.classes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 *
 * @author Gabriel
 */
public class Slack {
    private static HttpClient cliente = HttpClient.newHttpClient();
    private static final String url = "https://hooks.slack.com/services/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";
    
    public static void enviarMensagem(JSONObject content)throws IOException,InterruptedException{
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();
        
        HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println(String.format("Status: %s ", response.statusCode()));
        System.out.println(String.format("Response: %s", response.body()));
    }
}

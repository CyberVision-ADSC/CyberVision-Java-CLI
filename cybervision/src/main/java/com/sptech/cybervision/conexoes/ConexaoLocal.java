/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sptech.cybervision.conexoes;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author leona
 */
public class ConexaoLocal {
    
    private JdbcTemplate connection;

    public ConexaoLocal() {

        BasicDataSource dataSource = new BasicDataSource();

        dataSource​.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource​.setUrl("jdbc:mysql://172.17.0.2:3306/cybervision?allowPublicKeyRetrieval=true&useSSL=false?allowPublicKeyRetrieval=true&useSSL=false");
        //dataSource​.setUrl("jdbc:mysql://localhost:3306/cybervision");
        
        dataSource​.setUsername("root");

        dataSource​.setPassword("urubu100");

        this.connection = new JdbcTemplate(dataSource);

    }

    public JdbcTemplate getConnection() {

        return connection;

    }
    
}

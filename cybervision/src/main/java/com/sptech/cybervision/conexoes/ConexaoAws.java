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
public class ConexaoAws {
    
     private JdbcTemplate connection;

    public ConexaoAws() {

        
        try{
            BasicDataSource dataSource = new BasicDataSource();

            dataSource​.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource​.setUrl("jdbc:mysql://cybervision.c6w91j4rybl4.us-east-1.rds.amazonaws.com:3306/cybervision");

            dataSource​.setUsername("admin");

            dataSource​.setPassword("#Gfgrupo4");
        
            this.connection = new JdbcTemplate(dataSource);
            
           
            
        }catch(Exception e){
            System.out.println("Erro" + e);
            
        }
       
       

    }

    public JdbcTemplate getConnection() {

        
        return connection;

    }

    
}

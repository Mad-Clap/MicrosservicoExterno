package com.example.externo.service;

import org.springframework.stereotype.Service;

@Service ("prototype")
public class HelloWorld {
    private String hello;


    public String getHello(int param) {
        if(param==1) this.hello = "Hello World";
        if(param==2) this.hello = "Hello Heaven";
        if(param==3) this.hello = "Hello Hell";
        return hello;
    }
}

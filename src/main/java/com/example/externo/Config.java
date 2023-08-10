package com.example.externo;

import com.google.gson.Gson;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config {
    @Autowired
    private Environment env;
    @Bean
    public Gson gson(){
        return new Gson();
    }

    @Bean
    public MailgunMessagesApi mailgunMessagesApi() {
        return MailgunClient.config(env.getProperty("environment.mailgunAcess"))
                .createApi(MailgunMessagesApi.class);
    }

}

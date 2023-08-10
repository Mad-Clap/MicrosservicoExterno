package com.example.externo.service;

import com.example.externo.model.ReqEmail;
import com.example.externo.controllers.dto.NovoEmail;
import com.example.externo.repository.EmailRepository;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import feign.Response;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class ReqEmailService {

    @Autowired
    MailgunMessagesApi client;

    @Autowired
    EmailRepository repo;

    @Autowired
    private Environment env;


    public ReqEmailService() {
        //springboot
    }
    @Transactional
    public ReqEmail enviarMensagem(NovoEmail req) {

        String messageFrom = "noreply@PM.com";
        String mensagem = req.getMensagem();
        String assunto = req.getAssunto();
        String html ="null";

        Message message;
        ReqEmail email;

        if(mensagem.length()>=15) html = mensagem.substring(0,15);

        if(req.getMensagem().matches("\\d+/ativar")){
            int charAt = mensagem.indexOf('/');
            String id = mensagem.substring(0,charAt);

            message = Message.builder()
                    .from(messageFrom)
                    .to(req.getEmail())
                    .subject(assunto)
                    .html(mensagemAtivacao(id))
                    .build();
            email = new ReqEmail(req.getEmail(),req.getAssunto(), req.getMensagem());
        }
        else if(html.equals("<!DOCTYPE html>")){
             message = Message.builder()
                    .from(messageFrom)
                    .to(req.getEmail())
                    .subject(assunto)
                    .html(mensagem)
                    .build();
            email = new ReqEmail(req.getEmail(),req.getAssunto(), "html_email");
        }
        else {
             message = Message.builder()
                    .from(messageFrom)
                    .to(req.getEmail())
                    .subject(assunto)
                    .text(mensagem)
                    .build();
            email = new ReqEmail(req.getEmail(),req.getAssunto(), req.getMensagem());
        }

        try (Response res = client.sendMessageFeignResponse(env.getProperty("environment.emailDomain")
                , message)) {
            if (res.status() != 200) return null;
        }

        email = repo.save(email);

        return email;

    }


    public boolean ativarCiclista(Long id){
        HttpClient client = HttpClient.newHttpClient();

        /*cria a request*/
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create("https://microsservico-aluguel.vercel.app/ciclista/"+id+"/ativar"))
                .header("Content-Type","application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers
                        .noBody())
                .timeout(Duration.of(10, SECONDS))
                .build();

        /*client envia request*/
        CompletableFuture<HttpResponse<String>> responseFuture =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        /*Capta resposta*/
        HttpResponse<String> response;
        try {
            response = responseFuture.get();
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return response.statusCode() == 200;
    }

    /*Util*/
    private static String mensagemAtivacao(String id){
        return """
                <!DOCTYPE html>
                <html lang="pt-br" dir="ltr">
                  <head>
                    <meta charset="utf-8">
                    <meta name = "viewport" content = "width = device-width, initial-scale = 1.0">
                        <title>E-mail Ativado</title>
                    </head>
                    <body>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td align="center">
                                    <h1>Clique no botão para ativar sua conta</h1>
                                </td>
                            </tr>
                            <tr>
                                <td align="center" style="padding-top: 10%;">
                                    <a href="http://localhost:8080/ativarEmail/"""+id+"\""+"""
                                    target="_blank" style=" background-color: #008CBA; /* Green */
                                    border: none;
                                    color: white;
                                    padding: 15px 32px;
                                    text-align: center;
                                    text-decoration: none;
                                    display: inline-block;
                                    font-size: 16px;
                                    margin: 4px 2px;">
                                        Ativar
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </body>
                </html>        
                """;
    }
}

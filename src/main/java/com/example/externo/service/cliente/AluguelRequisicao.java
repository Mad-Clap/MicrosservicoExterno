package com.example.externo.service.cliente;

import com.example.externo.controllers.dto.Ciclista;
import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AluguelRequisicao {
    private static final String URL = "https://microsservico-aluguel.vercel.app";
    public static final  String CONTENT_TYPE = "Content-Type";

    @Autowired
    Gson parser;

    public AluguelRequisicao() {/*sem atributos na classe*/}

     public NovoCartaoDeCredito getCartaoByID(Long id){
         HttpClient client = HttpClient.newHttpClient();

         HttpRequest request = HttpRequest.newBuilder(
                         URI.create(URL+"/cartaoDeCredito/"+id))
                 .header(CONTENT_TYPE,"application/json")
                 .GET()
                 .timeout(Duration.of(10, SECONDS))
                 .build();

         //client envia request
         CompletableFuture <HttpResponse<String>> responseFuture =
                 client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

         //Capta resposta
         HttpResponse<String> response;
         try {
             response = responseFuture.get();
         } catch (ExecutionException e) {
             return null;
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
             return null;
         }
         if(response.statusCode()!=200) return null;

         return parser.fromJson(response.body(),NovoCartaoDeCredito.class);
     }

    public Ciclista getCiclista(Long id){

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(URL+"/ciclista/"+id))
                .header(CONTENT_TYPE,"application/json")
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();

        //client envia request
        CompletableFuture <HttpResponse<String>> responseFuture =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        /*Capta resposta*/
        HttpResponse<String> response;
        try {
            response = responseFuture.get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        if(response.statusCode()!=200) return null;

        return parser.fromJson(response.body(),Ciclista.class);

    }

}

package com.example.externo.service.cliente;


import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import static java.time.temporal.ChronoUnit.SECONDS;
@Service
public class GetNetRequisicao {


    @Autowired
    Gson parser;
    @Autowired
    private Environment env;
    private String URL;
    public static final  String ERRO = "ERRO";
    public static final  String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "authorization";

    private String sessionToken;
    private LocalDateTime sessionTime;

    public GetNetRequisicao() {/*sem atributos na classe*/}

    @PostConstruct
    public void init(){
        URL = env.getProperty("environment.getNet");
    }

    public boolean autoriza() {
        /*Refresca o valor dos atributos SESSION_TOKEN e SESSION_TIME se necessário*/
        if(sessionToken ==null || LocalDateTime.now().isAfter(sessionTime)){
            /* cria cliente de requisição */
            HttpClient client = HttpClient.newHttpClient();

            /*cria a request*/
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create(env.getProperty("environment.authGetNet")))
                    .header(CONTENT_TYPE,"application/x-www-form-urlencoded")
                    .header(AUTHORIZATION, "Basic "+env.getProperty("environment.auth_token"))
                    .POST(HttpRequest.BodyPublishers.ofString("scope=oob&grant_type=client_credentials"))
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
            if(response.statusCode()!=200) return false;

            /*Mapeia body da resposta*/
            Map<String,String> token = parser.fromJson(response.body(),Map.class);

            /*Atribui os novos valores nos atributos SESSION_TOKEN e SESSION_TIME*/
            sessionToken = "Bearer "+token.get("access_token");
            sessionTime = LocalDateTime.now().plusSeconds(36000);
            return true;
        }
        return true;
    }

    public String tokenizaCartao(String numeroCartao) {

        HttpClient client = HttpClient.newHttpClient();

        /*cria a request*/
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL+"/tokens/card"))
                .header(CONTENT_TYPE,"application/json; charset=utf-8")
                .header(AUTHORIZATION, sessionToken)
                .POST(HttpRequest.BodyPublishers.ofString("{\"card_number\":\"" + numeroCartao + "\"}"))
                .timeout(Duration.of(10, SECONDS))
                .build();

        /*client envia request*/
        CompletableFuture<HttpResponse<byte[]>> responseFuture =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());

        /*Capta a resposta*/
        HttpResponse<byte[]> response;
        try {
            response = responseFuture.get();
        } catch (ExecutionException e) {
            return ERRO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ERRO;
        }

        if(response.statusCode()!=201)return ERRO;

        /*Mapeia body da resposta*/
        byte[] in = (response.body());
        String convert;

        try {
            convert = decompress(in);
        } catch (IOException e) {
            return ERRO;
        }


        if(convert == null)return ERRO;

        List<String> array = List.of(convert.split(":"));
        String res = array.get(1);
        res = new StringBuilder(res).substring(1,res.length()-2);

        return res;
    }

   public Boolean verificaCartao(NovoCartaoDeCredito cartao, String cardToken){

        /* cria cliente de requisição */
        HttpClient client = HttpClient.newHttpClient();

        /* separa atributo "validade" em ano e mês */
        String month =new StringBuilder(cartao.getValidade()).substring(5,7);
        String year =new StringBuilder(cartao.getValidade()).substring(2,4);

        /*cria a request*/
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL+"/cards/verification"))
                .header(CONTENT_TYPE,"application/json;charset=utf-8")
                .header(AUTHORIZATION, sessionToken)
                .header("seller_id", env.getProperty("environment.sellerId"))
                .POST(HttpRequest.BodyPublishers
                        .ofString("{ \"number_token\": \""+cardToken+"\","+
                                "\"cardholder_name\": \""+cartao.getNomeTitular()+"\","+"" +
                                "\"expiration_month\": \""+month+"\","+
                                "\"expiration_year\": \""+year+"\","+
                                "\"security_code\": \""+cartao.getCvv()+"\"}"))
                .timeout(Duration.of(10, SECONDS))
                .build();

        /*client envia request*/
        CompletableFuture <HttpResponse<String>> responseFuture =
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
        if(response.statusCode()!=200) return false;

        /*Mapeia body da resposta*/
        Map<String,String> status= parser.fromJson(response.body(),Map.class);

        return status.get("status").equals("VERIFIED");
    }

    public String realizaPagamento(NovoCartaoDeCredito cartao, String cardToken, int valor)  {

        /*Gera corpo da requisição*/
        String req = geraJSONCobranca(cartao,cardToken,valor);

        /*cria cliente de requisição*/
        HttpClient client = HttpClient.newHttpClient();

        /*cria a request*/
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(URL+"/payments/credit"))
                .header("Accept", "application/json, text/plain, */*")
                .header("Authorization", sessionToken)
                .header(CONTENT_TYPE,"application/json")
                .header("seller_id", env.getProperty("environment.sellerId"))
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .timeout(Duration.of(10, SECONDS))
                .build();


        /*Client envia request e capta resposta*/
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            return ERRO;
        }
        if(response.statusCode()!=200) return ERRO;

        Map res = parser.fromJson(response.body(), Map.class);

        return res.get("status").toString();
    }

    /**Util**/
    private String decompress(byte[] str) throws IOException {
        if (str == null ) {
            return null;
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line=bf.readLine())!=null) {
            outStr.append(line);
        }
        return outStr.toString();
    }

    private String geraJSONCobranca(NovoCartaoDeCredito cartao, String cardToken, int valor){
        String month =new StringBuilder(cartao.getValidade()).substring(5,7);
        String year =new StringBuilder(cartao.getValidade()).substring(2,4);

        StringBuilder pagamentoBuilder =
                new StringBuilder().append("{" +
                                "    \"amount\": "+valor+",")
                        .append("\"credit\": {")
                        .append("    \"card\": "+"    {" +
                                "        \"cardholder_name\": \""+cartao.getNomeTitular()+"\"," +
                                "        \"expiration_month\": \""+month+"\"," +
                                "        \"expiration_year\": \""+year+"\"," +
                                "        \"security_code\": \""+cartao.getCvv()+"\"," +
                                "        \"number_token\": \""+cardToken+"\"" +
                                "    },")
                        .append(
                                "    \"delayed\": false," +"    " +
                                        "\"number_installments\": 1," +
                                        "    \"save_card_data\": false," +
                                        "    \"transaction_type\": \"FULL\"},")
                        .append("\"customer\": {")
                        .append("    \"billing_address\": " +
                                "{" +
                                "    \"street\": \"Av. Brasil\"," +
                                "    \"number\": \"1000\"," +
                                "    \"complement\": \"Sala 1\"," +
                                "    \"district\": \"São Geraldo\"," +
                                "    \"city\": \"Porto Alegre\"," +
                                "    \"state\": \"RS\"," +
                                "    \"country\": \"Brasil\"," +
                                "    \"postal_code\": \"90230060\"" +
                                "    },")
                        .append(
                                "   \"customer_id\":\"12345\"," +
                                        "\"document_number\":\"12345678912\"," +
                                        "\"document_type\":\"CPF\"," +
                                        "\"email\":\"aceitei@getnet.com.br\"," +
                                        "\"first_name\":\"João\"," +
                                        "\"last_name\":\"da Silva\"," +
                                        "\"phone_number\":\"5551999887766\"},\"device\": {},")
                        .append("\"order\": " +
                                "{" +
                                "    \"order_id\": \"12345\"" +
                                "}, \"seller_id\": "+"\""+ env.getProperty("environment.sellerId") +"\",")
                        .append(" \"shippings\":[" +
                                "{" +
                                "\"address\":{" +
                                "\"city\":\"Porto Alegre\"," +
                                "\"number\":\"1000\"," +
                                "\"postal_code\":\"90230060\"," +
                                "\"state\":\"RS\"," +
                                "\"street\":\"Av. Brasil\"" +
                                "}" + "}" + "]," + "\"sub_merchant\":{}" + "}");

        return pagamentoBuilder.toString();
    }




}

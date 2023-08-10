package com.example.externo.controllers;


import com.example.externo.service.HelloWorld;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping
public class HelloController {

     @Autowired
     HelloWorld service;
     @Autowired
     Gson parser;

    @GetMapping("/helloWorld")
    public @ResponseBody ResponseEntity getHelloWorld(@RequestParam(name = "mensagem", defaultValue = "1") int param){
        if(param !=1 && param !=2 && param !=3) return ResponseEntity.badRequest().body("não há essa opção");

        String response = service.getHello(param);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value= "", produces = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody String root(){

        return """
                <!DOCTYPE html>
                        <html lang="pt-br" dir="ltr">
                            <head>
                                <meta charset="utf-8">
                                <meta name = "viewport" content = "width = device-width, initial-scale = 1.0">
                                    <title>root Externo</title>
                            </head>
                            <body>
                                <h1 align="center" style="background-color: black;color: white;">Endpoints e testes:</h1>
                                <p>
                                    Endpoint <span style="color: brown;">POST</span> /enviarEmail<br>
                                    &nbspExemplo Json:<br>
                                    &nbsp&nbsp{"email": "leonardo.d.goncalves@edu.unirio.br", "assunto":"testePM", "mensagem":"testando essa parada"}<br>
                                    &nbsp&nbsp(E-mails só são enviados a endereços registrados no domínio da API)
                                </p>
                                <p>
                                    Endpoint <span style="color: brown;">POST</span> /cobranca<br>
                                    &nbspExemplo Json:
                                    &nbsp&nbsp{"valor":"55","ciclista":"46"}
                                </p>
                                <p>
                                    Endpoint <span style="color: brown;">POST</span> /filaCobranca<br>
                                    &nbspExemplo Json:
                                    &nbsp&nbsp{"valor":"444","ciclista":"46"}
                                    &nbsp&nbsp{"valor":"555","ciclista":"47"}
                                </p>
                                <p>
                                    Endpoint <span style="color: brown;">POST</span> /processaCobrancasEmFila<br>
                                    &nbspSem corpo de requisição (adicionar cobranças com /filaCobranca para o endpoint retornar OK - 200 e as cobranças pagas)
                                </p>
                                <p>
                                    Endpoint <span style="color: green;">GET</span> /cobranca/{id}<br>
                                    &nbspPossíveis IDs registrados: 1,2,52,53,54,102,152,153,202,203<br>
                                    &nbsp(caso não haja cobranças adicionar com /filaCobranca ou /cobranca)
                                </p>
                                <p>
                                    Endpoint <span style="color: brown;">POST</span> /validaCartaoDeCredito<br>
                                    &nbspExemplo Json:
                                    &nbsp{"cvv":"123", "nomeTitular":"Leo LIMA", "numero": "5155901222280001123", "validade": "2028-12-01"}
                                </p>
                            </body>
                        </html>
                """;
    }

}

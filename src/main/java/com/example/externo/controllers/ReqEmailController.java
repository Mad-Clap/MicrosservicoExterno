package com.example.externo.controllers;

import com.example.externo.model.ReqEmail;
import com.example.externo.controllers.dto.NovoEmail;
import com.example.externo.service.ReqEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping
public class ReqEmailController {

    public static final String DESCRIPTION = "description";
    @Autowired
    ReqEmailService service;

    @PostMapping(value = "/enviarEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity postvalidaCartaoDeCredito
            (@Valid @RequestBody NovoEmail req) {

        Optional<ReqEmail> res =Optional.ofNullable(service.enviarMensagem(req));
        if(res.isEmpty()){
            return ResponseEntity.status(404)
                    .header(DESCRIPTION,"E-mail não existe")
                    .body("{ \"Código\" : \"404\", \"Mensagem\" : \"E-mail não existe\" }");
        }
        else {
            return ResponseEntity.status(200)
                    .header(DESCRIPTION, "Externo solicitada")
                    .body(res.get());
        }

    }

    @GetMapping(value = "ativarEmail/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody String getValidaEmail(@PathVariable("id") Long id){
        boolean res = service.ativarCiclista(id);

        if(res){
            return ATIVADO;
        }
        else {
            return ERRO_ATIVACAO;
        }
    }
    public static final String ATIVADO =
            """
                <!DOCTYPE html>
                <html lang="pt-br" dir="ltr">
                  <head>
                    <meta charset="utf-8">
                    <meta name = "viewport" content = "width = device-width, initial-scale = 1.0">
                        <title>E-mail Ativado</title>
                    </head>
                    <body>
                        <main>
                            <p style="font-size: x-large;font-weight: bold;">
                                O seu e-mail foi ativado com sucesso em nosso serviço.
                             </p>
                        </main>
                    </body>
                </html>
                """;

    public static final String ERRO_ATIVACAO =
            """
                <!DOCTYPE html>
                <html lang="pt-br" dir="ltr">
                  <head>
                    <meta charset="utf-8">
                    <meta name = "viewport" content = "width = device-width, initial-scale = 1.0">
                        <title>E-mail Ativado</title>
                    </head>
                    <body>
                        <main>
                            <p style="font-size: x-large;font-weight: bold;">
                               Algo deu errado na ativação do seu e-mail, tente se recadastrar
                             </p>
                        </main>
                    </body>
                </html>
                """;
}

package com.example.externo.controllers;

import com.example.externo.model.Cobranca;
import com.example.externo.controllers.dto.NovaCobranca;
import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.example.externo.service.CobrancaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping
@Validated
public class CobrancaController {

    public static final String DESCRIPTION = "description";
    public static final String DADOS_INVALIDOS = "Dados Inválidos";
    @Autowired
    CobrancaService service;

    @GetMapping("/cobranca/{id}")
    @ResponseBody
    public ResponseEntity getCobranca(
            @PathVariable("id") long id) {

        Optional <Cobranca> res = Optional.ofNullable(service.recuperaCobranca(id));

        if(res.isEmpty())
            return ResponseEntity.status(404)
                    .header(DESCRIPTION, "Não encontrado")
                    .body(setError("404","Não Encontrado"));
        else
            return ResponseEntity.status(200)
                    .header(DESCRIPTION, "Cobrança")
                    .body(res.get());
    }


    @PostMapping(value = "/validaCartaoDeCredito", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity postvalidaCartaoDeCredito
            (@Valid @RequestBody NovoCartaoDeCredito req) {

        Boolean bool = service.validaCartao(req);

        if(Boolean.TRUE.equals(bool)){
            return ResponseEntity.status(200)
                    .header(DESCRIPTION, "Dados atualizados")
                    .body("Dados atualizados");

        }
        else {
            //"422" e "Dados Inválidos" são os valores padrão das Strings `codigo`
            // e `mensagem` de setError, caso seja passado apenas "" em algum dos parâmetros
            return ResponseEntity.status(422)
                    .header(DESCRIPTION, DADOS_INVALIDOS)
                    .body(setError("",""));
        }

        }


    @PostMapping(value = "/cobranca", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity postCobranca(@Valid @RequestBody NovaCobranca req) {

        Optional<Cobranca> res = Optional.ofNullable(service.realizaCobranca(req));
        if(res.isEmpty()){
            return ResponseEntity.status(422)
                    .header(DESCRIPTION, DADOS_INVALIDOS)
                    .body(setError("",""));
        }
        else {
            return ResponseEntity.status(200)
                    .header(DESCRIPTION, "Cobrança solicitada")
                    .body(res.get());
        }
    }

    @PostMapping(value = "/filaCobranca", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity postfilaCobranca(@RequestBody @Valid NovaCobranca req){

        Optional<Cobranca> res = Optional.ofNullable(service.addFila(req));
        if(res.isEmpty()){
            return ResponseEntity.status(422)
                    .header(DESCRIPTION, DADOS_INVALIDOS)
                    .body(setError("",""));

        }
        else{

            return ResponseEntity.status(200)
                    .header(DESCRIPTION, "Cobrança Incluida")
                    .body(res.get());
        }

    }

    @PostMapping(value = "/processaCobrancasEmFila", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity postprocessaCobrancasEmFila() {

            List<Cobranca> res = service.processaFila();
            if(res.isEmpty()){
                return ResponseEntity.status(422)
                        .header(DESCRIPTION, DADOS_INVALIDOS)
                        .body(setError("",""));
            }
            else{
                return ResponseEntity.status(200)
                        .header(DESCRIPTION, "Processamento concluído com sucesso")
                        .body(res);
            }
    }

    String setError (String codigo, String mensagem){
        if(mensagem.equals("")) mensagem = DADOS_INVALIDOS;
        if (codigo.equals("")) codigo = "422";

        return "{ \"Código\" : \""+codigo+"\", \"Mensagem\" : \""+mensagem+"\" }";

    }
}

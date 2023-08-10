package com.example.externo;

import com.example.externo.controllers.ReqEmailController;
import com.example.externo.model.ReqEmail;
import com.example.externo.controllers.dto.NovoEmail;
import com.example.externo.service.ReqEmailService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReqEmailController.class)
public class EmailControllerTeste {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Gson parser;

    @MockBean
    private ReqEmailService service;
    @Test
     void testaEnviaEmail() throws Exception {
        NovoEmail novoEmail = new NovoEmail("mockEmail@mail.com","Teste de envio de email",
                "Testando 1,2,3...");
        ReqEmail email = new ReqEmail("mockEmail@mail.com","Teste de envio de email",
                "Testando 1,2,3...");
        email.setId(1L);
        when(service.enviarMensagem(novoEmail)).thenReturn(email);

        this.mockMvc.perform(post("/enviarEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parser.toJson(novoEmail))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(parser.toJson(email)));

    }
    @Test
    void ativaEmail() throws Exception {
        NovoEmail novoEmail = new NovoEmail("mockEmail@mail.com","Teste ativa email",
                "46/ativar");
        ReqEmail email = new ReqEmail("mockEmail@mail.com","Teste de envio de email",
                "Testando 1,2,3...");
        email.setId(1L);
        when(service.ativarCiclista(46L)).thenReturn(true);

        this.mockMvc.perform(get("/ativarEmail/46"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ReqEmailController.ATIVADO)));
    }
}

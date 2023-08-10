package com.example.externo;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.externo.controllers.CobrancaController;
import com.example.externo.model.Cobranca;
import com.example.externo.controllers.dto.NovaCobranca;
import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.example.externo.model.Status;
import com.example.externo.service.CobrancaService;
import com.google.gson.Gson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(CobrancaController.class)
class CobrancaControllerTeste {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Gson parser;

    @MockBean
    private CobrancaService service;
    @Test
    void recuperaCobrancaTeste() throws Exception {
        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        c.setId(1L);
        when(service.recuperaCobranca(1L)).thenReturn(c);
        this.mockMvc.perform(get("/cobranca/{id}", "1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(parser.toJson(c)));
    }
    @Test
    void ProcessaFilaTeste() throws Exception {
        List<Cobranca> lista = new ArrayList<>();
        lista.add(new Cobranca(Status.PAGA,"1111","2222",45,1L));

        when(service.processaFila()).thenReturn(lista);
        this.mockMvc.perform(post("/processaCobrancasEmFila")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(parser.toJson(lista)));
    }
    @Test
    void validaCartaoTeste() throws Exception {
        NovoCartaoDeCredito valido =
                new NovoCartaoDeCredito("123", "Leo LIMA",
                        "5155901222280001123","2028-12-01");

        System.out.println(when(service.validaCartao(valido)).thenReturn(true));
        this.mockMvc.perform(post("/validaCartaoDeCredito")
                        .content("{\"cvv\":\"123\",\"nomeTitular\":\"Leo LIMA\",\"numero\":\"5155901222280001123\",\"validade\":\"2028-12-01\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dados atualizados")));
    }
    @Test
    void cobrancaTeste() throws Exception {
        NovaCobranca req = new NovaCobranca(44,1L);

        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        c.setId(1L);

        when(service.realizaCobranca(req)).thenReturn(c);

        this.mockMvc.perform(post("/cobranca").contentType(MediaType.APPLICATION_JSON)
                        .content(parser.toJson(req))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(parser.toJson(c)));
    }
    @Test
    void filaCobrancaTeste() throws Exception {
        NovaCobranca req = new NovaCobranca(44,1L);
        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        c.setId(1L);

        when(service.addFila(req)).thenReturn(c);

        this.mockMvc.perform(post("/filaCobranca").contentType(MediaType.APPLICATION_JSON)
                        .content(parser.toJson(req))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(parser.toJson(c)));
    }
}

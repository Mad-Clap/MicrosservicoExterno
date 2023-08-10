package com.example.externo;

import com.example.externo.model.Cobranca;
import com.example.externo.model.Status;
import com.example.externo.controllers.dto.Ciclista;
import com.example.externo.controllers.dto.NovaCobranca;
import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.example.externo.controllers.dto.Passaporte;
import com.example.externo.repository.CobrancaRepository;
import com.example.externo.service.cliente.AluguelRequisicao;
import com.example.externo.service.cliente.GetNetRequisicao;
import com.example.externo.service.CobrancaService;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {


    Gson parser = new Gson();
    @Mock
    CobrancaRepository repo;

    @Mock
    GetNetRequisicao pagamento;

    @Mock
    AluguelRequisicao aluguel;
    @InjectMocks
    CobrancaService service = new CobrancaService();


    @Test
    void realizaCobrancaTeste(){
        NovaCobranca req = new NovaCobranca(44,1L);
        NovoCartaoDeCredito valido =
                new NovoCartaoDeCredito("123", "Leo LIMA",
                        "5155901222280001123","2028-12-01");
        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        c.setId(1L);
        Ciclista cicli = new Ciclista(1L,"ATIVO","JOAO","2000-12-01","11122233344",
                new Passaporte("123","2028-12-01","Brasil"),"BRASILEIRO",
                "email@email.com","123456");

        when(aluguel.getCiclista(req.getCiclista())).thenReturn(cicli);
        when(aluguel.getCartaoByID(req.getCiclista())).thenReturn(valido);
        when(pagamento.autoriza()).thenReturn(true);
        when(pagamento.tokenizaCartao(valido.getNumero())).thenReturn("123");
        when(pagamento.realizaPagamento(valido,"123",req.getValor())).thenReturn("APPROVED");
        when(repo.save(any(Cobranca.class))).thenReturn(c);


        Cobranca res = service.realizaCobranca(req);
        assertNotNull(res);
        assertSame(c,res);
    }

    @Test
    void validaCartaoTeste(){
        NovoCartaoDeCredito valido =
                new NovoCartaoDeCredito("123", "Leo LIMA",
                        "5155901222280001123","2028-12-01");
        when(pagamento.autoriza()).thenReturn(true);
        when(pagamento.tokenizaCartao(valido.getNumero())).thenReturn("123");
        when(pagamento.verificaCartao(valido,"123")).thenReturn(true);
        Boolean res = service.validaCartao(valido);
        assertEquals(true,res);
    }

    @Test
    void recuperaCobrancaTesta(){

        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        Optional<Cobranca> opt = Optional.of(c);
        c.setId(1L);
        when(repo.findById(1L)).thenReturn(opt);

        Cobranca res = service.recuperaCobranca(1L);

        assertSame(res,c);
    }

    @Test
    void addFilaTeste(){

        NovaCobranca req = new NovaCobranca(44,1L);
        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:50:05",
                "19/06/2023 12:50:06",44,1L);
        c.setId(1L);

        when(repo.save(any(Cobranca.class))).thenReturn(c);

        Cobranca res = service.addFila(req);

        assertNotNull(res);
        assertSame(c,res);
    }

    @Test
    void processaFilaTeste(){

        NovoCartaoDeCredito valido =
                new NovoCartaoDeCredito("123", "Leo LIMA",
                        "5155901222280001123","2028-12-01");
        Cobranca a = new Cobranca(Status.PAGA,"19/06/2023 12:51:05",
                "19/06/2023 12:51:06",11,1L);
        Cobranca b = new Cobranca(Status.PAGA,"19/06/2023 12:52:05",
                "19/06/2023 12:52:06",22,2L);
        Cobranca c = new Cobranca(Status.PAGA,"19/06/2023 12:53:05",
                "19/06/2023 12:53:06",33,3L);
        c.setId(1L);
        List<Cobranca> list = new ArrayList<>();
        list.add(a);
        list.add(b); list.add(c);

        when(aluguel.getCartaoByID(any(Long.class))).thenReturn(valido);
        when(pagamento.autoriza()).thenReturn(true);
        when(pagamento.tokenizaCartao(valido.getNumero())).thenReturn("123");
        when(pagamento.realizaPagamento(eq(valido),eq("123"),anyInt())).thenReturn("APPROVED");
        when(repo.save(any(Cobranca.class))).thenReturn(a).thenReturn(b)
                .thenReturn(c).thenReturn(a).thenReturn(b).thenReturn(c);

        NovaCobranca req = new NovaCobranca(44,1L);
        service.addFila(req); service.addFila(req); service.addFila(req);
        List<Cobranca> res = service.processaFila();

        assertNotNull(res);
        assertEquals(list,res);

    }


}

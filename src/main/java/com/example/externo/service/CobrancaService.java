package com.example.externo.service;
import com.example.externo.model.Cobranca;
import com.example.externo.controllers.dto.Ciclista;
import com.example.externo.controllers.dto.NovaCobranca;
import com.example.externo.controllers.dto.NovoCartaoDeCredito;
import com.example.externo.model.Status;
import com.example.externo.repository.CobrancaRepository;
import com.example.externo.service.cliente.AluguelRequisicao;
import com.example.externo.service.cliente.GetNetRequisicao;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CobrancaService {
    private List<Cobranca> fila = new ArrayList<>();
    public static final String ERRO = "ERRO";




    @Autowired
    CobrancaRepository repo;

    @Autowired
    GetNetRequisicao pagamento;

    @Autowired
    AluguelRequisicao aluguel;


    public CobrancaService() {//construtor vazio por causa do Spring, que saiba
    }

    @PostConstruct
    public void init(){
        List<Cobranca> c = repo.findByStatus(Status.PENDENTE);
        if(!c.isEmpty()){
            fila = c;
        }
    }


    public Cobranca realizaCobranca(NovaCobranca novaCobranca) {
        if(!pagamento.autoriza()) return null;

        Optional<Ciclista> ciclista = Optional.ofNullable(aluguel.getCiclista(novaCobranca.getCiclista()));
        if(ciclista.isEmpty()) return null;
        if (!ciclista.get().getStatus().equals("ATIVO")) return null;

        /* Atribui valores necessários e falha caso não consiga*/

        NovoCartaoDeCredito cartao = aluguel.getCartaoByID(novaCobranca.getCiclista());
        if(cartao == null) return null;

        String numeroCartao = cartao.getNumero();
        String cardToken=pagamento.tokenizaCartao(numeroCartao);
        if(cardToken.equals(ERRO)) return null;

        /*Salva hora do início e do fim da transação e a realiza**/
        String horaSolicitacao = informaDataEHora();
        String pago = pagamento.realizaPagamento(cartao,cardToken,novaCobranca.getValor());
        String horaFinalizacao = informaDataEHora();
        /*Retorna null caso a chamada da api falhe **/

        if(pago.equals(ERRO)) return null;


        /*Salva entidade Cobranca no banco de dados e possivelmente
           na fila de cobranças pendentes e a retorna**/

        Cobranca res;
        if(pago.equals("APPROVED")) {
            res = new Cobranca(Status.PAGA, horaSolicitacao, horaFinalizacao,
                    novaCobranca.getValor(), novaCobranca.getCiclista());
            res = repo.save(res);
        }
        else {
            res = new Cobranca(Status.PENDENTE, horaSolicitacao, horaFinalizacao,
                    novaCobranca.getValor(), novaCobranca.getCiclista());
            res = repo.save(res);
            fila.add(res);
        }
        return res;



    }

    public Boolean validaCartao(NovoCartaoDeCredito cartao) {

        if(pagamento.autoriza()){
            String token= pagamento.tokenizaCartao(cartao.getNumero());
            if(token.equals(ERRO)) return false;

            return pagamento.verificaCartao(cartao,token);

        }
        return false;
    }
    @Transactional
    public Cobranca recuperaCobranca(Long id){
        Optional<Cobranca> optional = repo.findById(id);
        return optional.orElse(null);
    }

    public Cobranca addFila(NovaCobranca req) {

        String hora = informaDataEHora();
        Cobranca res = new Cobranca(Status.PENDENTE, hora, hora,
                req.getValor(), req.getCiclista());
        res = repo.save(res);
        fila.add(res);
        return res;
    }

    public List<Cobranca> processaFila() {
        List<Cobranca> res = new ArrayList<>();

        if(fila.isEmpty())
            return fila;

        int tamanho = fila.size()-1;
        for (int i=tamanho;i>=0;i--) {
            Cobranca cobranca = realizaCobrancaEmFila(fila.get(i));
            if (cobranca != null) {
                res.add(cobranca);
                fila.remove(i);
            }

        }
        return res;
    }

    private Cobranca realizaCobrancaEmFila(Cobranca cobranca) {
        if(!pagamento.autoriza()) return null;

        /* Atribui valores necessários e falha caso não consiga*/

        NovoCartaoDeCredito cartao = aluguel.getCartaoByID(cobranca.getCiclista());
        if(cartao == null) return null;

        String numeroCartao = cartao.getNumero();
        String cardToken=pagamento.tokenizaCartao(numeroCartao);
        if(cardToken.equals(ERRO)) return null;

        /*Salva hora do início e do fim da transação e a realiza*/
        String horaSolicitacao = informaDataEHora();
        String pago = pagamento.realizaPagamento(cartao,cardToken,cobranca.getValor());
        String horaFinalizacao = informaDataEHora();
        /*Retorna null caso a chamada da api falhe*/
        if(pago.equals(ERRO)) return null;


        /*Atualiza a entidade Cobranca no banco de dados e a retorna*/


        if(pago.equals("DENIED") || pago.equals("ERROR") || pago.equals("CANCELED")) {
            return null;
        }
        else {
            cobranca.setStatus(Status.PAGA);
            cobranca.setHoraSolicitacao(horaSolicitacao);
            cobranca.setHoraFinalizacao(horaFinalizacao);
            cobranca = repo.save(cobranca);
            return cobranca;
        }


    }

    /** Util **/
    private String informaDataEHora(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        return dtf.format(now);
    }

}

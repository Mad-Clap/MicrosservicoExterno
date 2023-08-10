package com.example.externo.repository;

import com.example.externo.model.Cobranca;
import com.example.externo.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    @Override
    Optional<Cobranca> findById(Long aLong);

    List<Cobranca> findByStatus(Status status);


}
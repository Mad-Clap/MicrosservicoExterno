package com.example.externo.repository;

import com.example.externo.model.ReqEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<ReqEmail, Long> {
}
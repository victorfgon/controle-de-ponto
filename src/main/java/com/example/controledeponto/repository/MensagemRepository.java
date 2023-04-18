package com.example.controledeponto.repository;

import com.example.controledeponto.model.Mensagem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemRepository extends MongoRepository<Mensagem, String> {
}


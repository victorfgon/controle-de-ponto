package com.example.controledeponto.repository;

import com.example.controledeponto.model.Relatorio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioRepository extends MongoRepository<Relatorio, String> {
}


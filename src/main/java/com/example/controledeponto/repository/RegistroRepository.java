package com.example.controledeponto.repository;

import com.example.controledeponto.model.Registro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends MongoRepository<Registro, String> {
    Registro findByDia(String dia);
}

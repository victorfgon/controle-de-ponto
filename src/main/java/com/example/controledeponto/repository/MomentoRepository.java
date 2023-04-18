package com.example.controledeponto.repository;

import com.example.controledeponto.model.Momento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MomentoRepository extends MongoRepository<Momento, String> {
}

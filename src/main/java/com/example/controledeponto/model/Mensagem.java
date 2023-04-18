package com.example.controledeponto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Document(collection = "mensagens")
public class Mensagem {

    @NotBlank
    @Schema(description = "Mensagem de erro ou sucesso")
    private String mensagem;
}
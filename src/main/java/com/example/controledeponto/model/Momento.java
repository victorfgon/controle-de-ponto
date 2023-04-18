package com.example.controledeponto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "momentos")
public class Momento {

    @Id
    private String id;

    @NotNull
    @Schema(description = "Data e hora da batida", example = "2018-08-22T08:00:00")
    private String dataHora;
}
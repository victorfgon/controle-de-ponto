package com.example.controledeponto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Document(collection = "relatorios")
public class Relatorio {

    @Id
    private String id;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}$")
    @Schema(description = "Mês do relatório", example = "2018-08")
    private String mes;

    @NotNull
    @Schema(description = "Horas trabalhadas no mês", example = "PT69H35M5S")
    private String horasTrabalhadas;

    @NotNull
    @Schema(description = "Horas excedentes no mês", example = "PT25M5S")
    private String horasExcedentes;

    @NotNull
    @Schema(description = "Horas devidas no mês", example = "PT0S")
    private String horasDevidas;

    @NotNull
    @Size(min = 1)
    @Schema(description = "Registros de jornada de trabalho do mês")
    private List<@NotNull Registro> registros;
}

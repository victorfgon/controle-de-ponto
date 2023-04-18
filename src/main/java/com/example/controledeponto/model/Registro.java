package com.example.controledeponto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "registros")
public class Registro {

    @Id
    private String id;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    @Schema(description = "Data da jornada de trabalho", example = "2018-08-22")
    private String dia;

    @NotNull
    @Size(min = 2, max = 4)
    @Schema(description = "Horários da jornada de trabalho", example = "[\"08:00:00\", \"12:00:00\", \"13:00:00\", \"18:00:00\"]")
    private List<@NotNull @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$") String> horarios;
}
package com.example.controledeponto.controller;

import com.example.controledeponto.exception.BadRequestException;
import com.example.controledeponto.exception.ConflictException;
import com.example.controledeponto.exception.ForbiddenException;
import com.example.controledeponto.model.Mensagem;
import com.example.controledeponto.model.Momento;
import com.example.controledeponto.model.Registro;
import com.example.controledeponto.model.Relatorio;
import com.example.controledeponto.service.BatidasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;

@Api(value = "Batidas", tags = { "Batidas" })
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class BatidasController {

    @Autowired
    private final BatidasService batidasService;

    private final Logger logger = LoggerFactory.getLogger(BatidasController.class);

    @PostMapping("/batidas")
    @ApiOperation(value = "Bater ponto", response = Registro.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Registro.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Mensagem.class),
            @ApiResponse(code = 403, message = "Forbidden", response = Mensagem.class),
            @ApiResponse(code = 409, message = "Conflict", response = Mensagem.class)
    })
    public ResponseEntity<Object> insereBatida(@Valid @RequestBody Momento momento) {
        log.info("POST /v1/batidas - {}", momento);
        try {
            Registro registro = batidasService.registrarBatida(momento);
            logger.info("Ponto batido com sucesso - {}", registro);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (BadRequestException e) {
            logger.warn("Erro ao bater ponto - {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new Mensagem(e.getMessage()));
        } catch (ConflictException e) {
            logger.warn("Erro ao bater ponto - {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new Mensagem(e.getMessage()));
        } catch (ForbiddenException e) {
            logger.warn("Erro ao bater ponto - {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new Mensagem(e.getMessage()));
        }
    }

    @GetMapping("/folhas-de-ponto/{mes}")
    @ApiOperation(value = "Relatório mensal", notes = "Geração de relatório mensal de usuário.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Relatório mensal", response = Relatorio.class),
            @ApiResponse(code = 404, message = "Relatório não encontrado")
        })
        public ResponseEntity<Relatorio> geraRelatorioMensal(@PathVariable String mes) {
            logger.info("Recebida requisição para gerar relatório mensal do mês {}", mes);
            try {
                Relatorio relatorio = batidasService.gerarRelatorioMensal(mes);
                logger.info("Relatório gerado com sucesso");
                return ResponseEntity.ok(relatorio);
            } catch ( HttpClientErrorException e){
                logger.error("Relatório não encontrado para o mês {}", mes);
                return ResponseEntity.notFound().build();
            }
        }

}
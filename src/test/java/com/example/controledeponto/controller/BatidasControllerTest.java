package com.example.controledeponto.controller;

import com.example.controledeponto.exception.BadRequestException;
import com.example.controledeponto.exception.ConflictException;
import com.example.controledeponto.exception.ForbiddenException;
import com.example.controledeponto.model.Mensagem;
import com.example.controledeponto.model.Momento;
import com.example.controledeponto.model.Registro;
import com.example.controledeponto.model.Relatorio;
import com.example.controledeponto.service.BatidasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatidasControllerTest {

    @Mock
    private BatidasService batidasService;

    @InjectMocks
    private BatidasController batidasController;

    @Test
    void testInsereBatida() {
        Momento momento = new Momento("1", "2022-01-01T08:00:00");
        Registro registro = new Registro("1", "2022-01-01", List.of("08:00:00"));

        when(batidasService.registrarBatida(momento)).thenReturn(registro);

        ResponseEntity<Object> responseEntity = batidasController.insereBatida(momento);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(registro, responseEntity.getBody());
        verify(batidasService, times(1)).registrarBatida(momento);
    }

    @Test
    void testInsereBatidaBadRequestException() {
        Momento momento = new Momento("1", "gfhdfghfh");
        String mensagemErro = "Erro ao bater ponto";
        BadRequestException badRequestException = new BadRequestException(mensagemErro);

        when(batidasService.registrarBatida(momento)).thenThrow(badRequestException);

        ResponseEntity<Object> responseEntity = batidasController.insereBatida(momento);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(new Mensagem(mensagemErro), responseEntity.getBody());
        verify(batidasService, times(1)).registrarBatida(momento);
    }

    @Test
    void testInsereBatidaConflictException() {
        Momento momento = new Momento("1", "2022-01-01T08:00:00");
        String mensagemErro = "Erro ao bater ponto";
        ConflictException conflictException = new ConflictException(mensagemErro);

        when(batidasService.registrarBatida(momento)).thenThrow(conflictException);

        ResponseEntity<Object> responseEntity = batidasController.insereBatida(momento);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(new Mensagem(mensagemErro), responseEntity.getBody());
        verify(batidasService, times(1)).registrarBatida(momento);
    }

    @Test
    void testInsereBatidaForbiddenException() {
        Momento momento = new Momento("1", "2022-01-01T08:00:00");
        String mensagemErro = "Erro ao bater ponto";

        when(batidasService.registrarBatida(momento)).thenThrow(new ForbiddenException(mensagemErro));
        ResponseEntity<Object> response = batidasController.insereBatida(momento);

        verify(batidasService, times(1)).registrarBatida(momento);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new Mensagem(mensagemErro), response.getBody());
    }

    @Test
    void testGeraRelatorioMensal() {
        String mes = "01-2022";
        Relatorio relatorio = new Relatorio();
        relatorio.setMes(mes);
        relatorio.setHorasTrabalhadas("PT80H30M");
        relatorio.setHorasExcedentes("PT5H30M");
        relatorio.setHorasDevidas("PT0S");

        Registro registro = new Registro("1", "2022-01-01", List.of("08:00:00"));
        relatorio.setRegistros(Collections.singletonList(registro));

        when(batidasService.gerarRelatorioMensal(mes)).thenReturn(relatorio);
        ResponseEntity<Relatorio> response = batidasController.geraRelatorioMensal(mes);

        verify(batidasService, times(1)).gerarRelatorioMensal(mes);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(relatorio, response.getBody());
    }

    @Test
    void testGeraRelatorioMensalNotFound() {
        String mes = "01-2022";

        when(batidasService.gerarRelatorioMensal(mes)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        ResponseEntity<Relatorio> response = batidasController.geraRelatorioMensal(mes);

        verify(batidasService, times(1)).gerarRelatorioMensal(mes);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


}


package com.example.controledeponto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.controledeponto.model.Relatorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.controledeponto.exception.BadRequestException;
import com.example.controledeponto.exception.ConflictException;
import com.example.controledeponto.exception.ForbiddenException;
import com.example.controledeponto.model.Momento;
import com.example.controledeponto.model.Registro;
import com.example.controledeponto.repository.MomentoRepository;
import com.example.controledeponto.repository.RegistroRepository;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class BatidasServiceTest {
    @Autowired
    @InjectMocks
    private BatidasService batidasService;

    @Mock
    private RegistroRepository registroRepository;

    @Mock
    private MomentoRepository momentoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegistrarBatidaNova() {
        Momento momento = new Momento("1", "2022-05-17T08:00:00");
        LocalDateTime dataHora = LocalDateTime.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate data = dataHora.toLocalDate();
        Registro registro = new Registro();
        registro.setDia(data.toString());
        registro.setHorarios(new ArrayList<>());
        registro.getHorarios().add(dataHora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        when(registroRepository.findByDia(data.toString())).thenReturn(null);

        Registro result = batidasService.registrarBatida(momento);

        verify(registroRepository).findByDia(data.toString());
        verify(registroRepository).save(registro);
        assertNull(result.getId());
        assertEquals(data.toString(), result.getDia());
        assertEquals(1, result.getHorarios().size());
        assertEquals(dataHora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")), result.getHorarios().get(0));
    }

    @Test
    void testRegistrarMaisUmaBatida() {
        Momento momento = new Momento("1", "2022-05-17T10:00:00");
        LocalDateTime dataHora = LocalDateTime.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate data = dataHora.toLocalDate();
        Registro registro = new Registro();
        registro.setDia(data.toString());
        registro.setHorarios(new ArrayList<>());
        registro.getHorarios().add("08:00:00");
        when(registroRepository.findByDia(data.toString())).thenReturn(registro);

        Registro result = batidasService.registrarBatida(momento);

        verify(registroRepository).findByDia(data.toString());
        verify(registroRepository).save(registro);
        assertNull(result.getId());
        assertEquals(data.toString(), result.getDia());
        assertEquals(2, result.getHorarios().size());
        assertEquals("08:00:00", result.getHorarios().get(0));
        assertEquals(dataHora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")), result.getHorarios().get(1));
    }

    @Test
    void testValidarMomentoNull() {
        Momento momento = null;
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            batidasService.registrarBatida(momento);
        });
        assertEquals("Campo obrigatório não informado", thrown.getMessage());
    }

    @Test
    void testValidarMomentoDataHoraInvalido() {
        Momento momento = new Momento();
        momento.setDataHora("2022-04-17");
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            batidasService.registrarBatida(momento);
        });
        assertEquals("Data e hora em formato inválido", thrown.getMessage());
    }

    @Test
    void testValidarRegistroMaisDe4Horarios() {
        Registro registro = new Registro();
        registro.setDia("2022-04-17");
        List<String> horarios = new ArrayList<>();
        horarios.add("08:00:00");
        horarios.add("12:00:00");
        horarios.add("13:00:00");
        horarios.add("17:00:00");
        horarios.add("18:00:00");
        registro.setHorarios(horarios);
        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            batidasService.validarRegistro(registro);
        });
        assertEquals("Apenas 4 horários podem ser registrados por dia", thrown.getMessage());
    }

    @Test
    void testValidarRegistroHorarioDuplicado() {
        Registro registro = new Registro();
        registro.setDia("2022-04-17");
        List<String> horarios = new ArrayList<>();
        horarios.add("17:00:00");
        horarios.add("17:00:00");
        registro.setHorarios(horarios);
        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            batidasService.validarRegistro(registro);
        });
        assertEquals("Apenas 4 horários podem ser registrados por dia", thrown.getMessage());
    }

    @Test
    void testValidarRegistroSabado() {
        Registro registro = new Registro();
        registro.setDia("2022-04-16");
        List<String> horarios = new ArrayList<>();
        horarios.add("08:00:00");
        horarios.add("12:00:00");
        horarios.add("13:00:00");
        horarios.add("17:00:00");
        registro.setHorarios(horarios);
        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            batidasService.validarRegistro(registro);
        });
        assertEquals("Sábado e domingo não são permitidos como dia de trabalho", thrown.getMessage());
    }

    @Test
    void testValidarRegistroEmDiaDeSemanaComMenosDeUmaHoraDeAlmoco() {
        Registro registro = new Registro();
        registro.setDia("2022-04-18");
        List<String> horarios = Arrays.asList("08:00:00", "08:30:00");
        registro.setHorarios(horarios);

        assertThrows(ForbiddenException.class, () -> {
            batidasService.validarRegistro(registro);
        });
    }

    @Test
    void testGerarRelatorioMensalComSucesso() {
        LocalDate now = LocalDate.now();
        String mes = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Registro registro = new Registro();
        registro.setDia(now.toString());
        List<String> horarios = new ArrayList<>();
        horarios.add("08:00:00");
        horarios.add("12:00:00");
        horarios.add("13:00:00");
        horarios.add("17:00:00");
        registro.setHorarios(horarios);
        doReturn(registro).when(registroRepository).findByDia(any());

        Relatorio relatorio = batidasService.gerarRelatorioMensal(mes);

        assertNotNull(relatorio);
        assertEquals(mes, relatorio.getMes());
    }

    @Test
    void testGerarRelatorioMensalSemRegistros() {
        String mes = "2023-01";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> batidasService.gerarRelatorioMensal(mes));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Nenhum registro encontrado para o mês especificado.", exception.getMessage());
    }
}


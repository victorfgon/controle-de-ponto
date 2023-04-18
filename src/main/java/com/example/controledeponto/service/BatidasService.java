package com.example.controledeponto.service;

import com.example.controledeponto.exception.BadRequestException;
import com.example.controledeponto.exception.ConflictException;
import com.example.controledeponto.exception.ForbiddenException;
import com.example.controledeponto.model.Momento;
import com.example.controledeponto.model.Registro;
import com.example.controledeponto.model.Relatorio;
import com.example.controledeponto.repository.MomentoRepository;
import com.example.controledeponto.repository.RegistroRepository;
import com.example.controledeponto.repository.RelatorioRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Api(tags = "Batidas de Ponto")
public class BatidasService {
    private final Logger logger = LoggerFactory.getLogger(BatidasService.class);

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private MomentoRepository momentoRepository;

    @Autowired
    private RelatorioRepository relatorioRepository;

    @ApiOperation(value = "Registra uma nova batida de ponto")
    public Registro registrarBatida(@ApiParam(value = "Momento da batida", required = true) Momento momento) {

        logger.info("Iniciando processo de registro de batida de ponto");

        validarMomento(momento);
        LocalDate data = LocalDate.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        logger.debug("Data da batida: {}", data);

        Registro registro = registroRepository.findByDia(data.toString());
        if (registro == null) {
            registro = criarRegistro(momento);
            logger.debug("Novo registro criado: {}", registro);
        } else {
            LocalDateTime dataHora = LocalDateTime.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalTime hora = dataHora.toLocalTime();
            registro.getHorarios().add(hora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            logger.debug("Batida adicionada ao registro existente: {}", registro);
        }
        validarRegistro(registro);
        registroRepository.save(registro);
        logger.info("Batida de ponto registrada com sucesso");
        return registro;
    }

    private void validarMomento(Momento momento) {
        if (momento == null || momento.getDataHora() == null) {
            throw new BadRequestException("Campo obrigatório não informado");
        }
        try {
            LocalDateTime.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Data e hora em formato inválido");
        }
        momentoRepository.save(momento);
        logger.debug("Momento salvo: {}", momento);
    }

    private Registro criarRegistro(Momento momento) {
        LocalDateTime dataHora = LocalDateTime.parse(momento.getDataHora(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate data = dataHora.toLocalDate();
        LocalTime hora = dataHora.toLocalTime();
        Registro registro = new Registro();
        registro.setDia(data.toString());
        registro.setHorarios(new ArrayList<>());
        registro.getHorarios().add(hora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        logger.debug("Novo registro criado: {}", registro);
        return registro;
    }
    void validarRegistro(Registro registro) {
        if (registro.getHorarios().size() > 4) {
            logger.error("Apenas 4 horários podem ser registrados por dia. Horários registrados: {}", registro.getHorarios());
            throw new ForbiddenException("Apenas 4 horários podem ser registrados por dia");
        }
        List<String> horarios = registro.getHorarios();
        Set<String> uniqueHorarios = new HashSet<>(horarios);
        if (uniqueHorarios.size() < horarios.size()) {
            logger.error("Horário já registrado. Horários registrados: {}", registro.getHorarios());
            throw new ConflictException("Horário já registrado");
        }
        if (!LocalDate.parse(registro.getDia()).getDayOfWeek().equals(DayOfWeek.SATURDAY) && !LocalDate.parse(registro.getDia()).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            if (registro.getHorarios().size() >= 2) {
                LocalTime ultimoHorario = LocalTime.parse(registro.getHorarios().get(registro.getHorarios().size() - 1), DateTimeFormatter.ISO_LOCAL_TIME);
                LocalTime penultimoHorario = LocalTime.parse(registro.getHorarios().get(registro.getHorarios().size() - 2), DateTimeFormatter.ISO_LOCAL_TIME);
                if (Duration.between(penultimoHorario, ultimoHorario).toMinutes() < 60) {
                    logger.error("Deve haver no mínimo 1 hora de almoço. Horários registrados: {}", registro.getHorarios());
                    throw new ForbiddenException("Deve haver no mínimo 1 hora de almoço");
                }
            }
        } else {
            logger.error("Sábado e domingo não são permitidos como dia de trabalho. Data registrada: {}", registro.getDia());
            throw new ForbiddenException("Sábado e domingo não são permitidos como dia de trabalho");
        }
    }
    @ApiOperation(value = "Gera relatório mensal de horas trabalhadas", response = Relatorio.class)
    public Relatorio gerarRelatorioMensal(String mes) {
        logger.info("Iniciando geração do relatório para o mês de {}", mes);
        List<Registro> diasDoMes = new ArrayList<>();
        LocalDate dataInicial = LocalDate.parse(mes + "-01");
        LocalDate dataFinal = dataInicial.withDayOfMonth(dataInicial.lengthOfMonth());

        for (LocalDate data = dataInicial; !data.isAfter(dataFinal); data = data.plusDays(1)) {
            Registro registro = registroRepository.findByDia(data.toString());
            if (registro != null) {
                diasDoMes.add(registro);
            }
        }
        if(diasDoMes.isEmpty()) {
            logger.warn("Não foram encontrados registros para o mês de {}", mes);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Nenhum registro encontrado para o mês especificado.");
        }

        Relatorio relatorio = new Relatorio();
        Duration horasTrabalhadas = Duration.parse("PT0H0M0S");
        Duration horasUteisMes = getHorasDeTrabalhoMes(dataInicial);
        for (Registro dias : diasDoMes) {
            if(dias.getHorarios().size() == 4){
                horasTrabalhadas = horasTrabalhadas.plus(Duration.between(LocalTime.parse(dias.getHorarios().get(0)), LocalTime.parse(dias.getHorarios().get(1))));
                horasTrabalhadas = horasTrabalhadas.plus(Duration.between(LocalTime.parse(dias.getHorarios().get(2)), LocalTime.parse(dias.getHorarios().get(3))));
            }
        }
        relatorio.setMes(mes);
        relatorio.setHorasTrabalhadas(horasTrabalhadas.toString());
        relatorio.setRegistros(diasDoMes);
        if(horasTrabalhadas.toSeconds() < horasUteisMes.toSeconds()){
            relatorio.setHorasExcedentes("0");
            relatorio.setHorasDevidas(horasUteisMes.minus(horasTrabalhadas).toString());
        }else{
            relatorio.setHorasExcedentes(horasTrabalhadas.minus(horasUteisMes).toString());
            relatorio.setHorasDevidas("0");
        }
        logger.info("Relatório gerado com sucesso para o mês de {}", mes);
        return relatorioRepository.save(relatorio);
    }

    public static Duration getHorasDeTrabalhoMes(LocalDate mes) {
        int diasUteis = 0;
        int diasNoMes = mes.lengthOfMonth();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate data = mes.withDayOfMonth(dia);

            DayOfWeek diaDaSemana = data.getDayOfWeek();
            if (diaDaSemana != DayOfWeek.SATURDAY && diaDaSemana != DayOfWeek.SUNDAY) {
                diasUteis++;
            }
        }
        return Duration.ofSeconds(diasUteis* 8L);
    }

}
package com.example.exchangeratesserver.client;

import com.example.exchangeratesserver.client.exception.FutureDateRequestException;
import com.example.exchangeratesserver.client.exception.ResponseParsingException;
import com.example.exchangeratesserver.client.exception.IllegalStartEndDate;
import com.example.exchangeratesserver.client.model.ValuteCode;
import com.example.exchangeratesserver.client.model.ValuteCursDynamicXml;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import com.example.exchangeratesserver.client.request.GetCursDynamicXmlRequest;
import com.example.exchangeratesserver.client.request.GetCursOnDateXmlRequest;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class CentralBankClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final XmlMapper xmlMapper = new XmlMapper();

    public CentralBankClient() {
        xmlMapper.registerModule(new JavaTimeModule());
    }

    /**
     * @param date дата, на которую нужно получить курс
     * @return Курс валют за указанную дату. Также, может вернуть курс за дату раньше указанной, это зависит от центробанка.
     * @throws FutureDateRequestException                         дата указана в будущем
     * @throws org.springframework.web.client.RestClientException ошибка при отправке запроса
     * @throws ResponseParsingException                           не удалось распарсить ответ
     */
    public ValuteDataXml getValuteData(LocalDate date) {
        LocalDate now = LocalDate.now();
        if (date.isAfter(now)) {
            throw new FutureDateRequestException(
                    format("Дата не может быть в будущем. Текущая дата - '%s', указанная - '%s'", now, date)
            );
        }

        HttpEntity<String> request = new HttpEntity<>(
                GetCursOnDateXmlRequest.getBody(date), GetCursOnDateXmlRequest.getHeaders()
        );
        ResponseEntity<String> response = restTemplate.exchange(
                GetCursOnDateXmlRequest.URI, GetCursOnDateXmlRequest.METHOD, request, String.class
        );
        log.info("Получен ответ от центробанка по запросу GetCursOnDateXML");

        return GetCursOnDateXmlRequest.parseResponse(response.getBody(), xmlMapper);
    }

    /**
     * Получение динамики курса указанной валюты за указанный период
     * @throws FutureDateRequestException                         дата to указана в будущем
     * @throws IllegalStartEndDate                        дата начала раньше даты конца
     * @throws org.springframework.web.client.RestClientException ошибка при отправке запроса
     * @throws ResponseParsingException                           не удалось распарсить ответ
     */
    public List<ValuteCursDynamicXml> getValuteCursDynamic(LocalDate from, LocalDate to, ValuteCode code) {
        if (to.isBefore(from) || to.isEqual(from)) {
            throw new IllegalStartEndDate(
                    format("Начальная дата не может быть раньше или равна конечной. Начальная дата - '%s', конечная - '%s'", from, to)
            );
        }

        HttpEntity<String> request = new HttpEntity<>(
                GetCursDynamicXmlRequest.getBody(code, from, to), GetCursDynamicXmlRequest.getHeaders()
        );
        ResponseEntity<String> response = restTemplate.exchange(
                GetCursDynamicXmlRequest.URI, GetCursDynamicXmlRequest.METHOD, request, String.class
        );
        log.info("Получен ответ от центробанка по запросу GetCursDynamicXml");

        return GetCursDynamicXmlRequest.parseResponse(response.getBody(), xmlMapper);
    }
}

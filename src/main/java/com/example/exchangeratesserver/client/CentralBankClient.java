package com.example.exchangeratesserver.client;

import com.example.exchangeratesserver.client.exception.FutureDateRequestException;
import com.example.exchangeratesserver.client.exception.ResponseParsingException;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import com.example.exchangeratesserver.client.model.ValuteRateXml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Service
public class CentralBankClient {
    private static final String BODY = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
              <soap12:Body>
                <GetCursOnDateXML xmlns="http://web.cbr.ru/">
                  <On_date>%s</On_date>
                </GetCursOnDateXML>
              </soap12:Body>
            </soap12:Envelope>""";

    private static final String SOAP_ENDPOINT = "https://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
    private static final String SOAPACTION = "http://web.cbr.ru/GetCursOnDate";
    private static final String CONTENT_TYPE = "application/soap+xml; charset=utf-8";

    private final RestTemplate restTemplate = new RestTemplate();
    private final XmlMapper xmlMapper = new XmlMapper();

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
                    String.format("Дата не может быть в будущем. Текущая дата - '%s', указанная - '%s'", now, date)
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", CONTENT_TYPE);
        headers.set("SOAPACTION", SOAPACTION);
        HttpEntity<String> request = new HttpEntity<>(String.format(BODY, date), headers);
        ResponseEntity<String> response = restTemplate.exchange(SOAP_ENDPOINT, POST, request, String.class);
        log.info("Получен ответ от центробанка: {}", response);

        return parseXmlResponse(response.getBody());
    }

    /**
     * Функция для парсинга ответа центробанка по запросу GetCursOnDateXML
     * @throws ResponseParsingException не удалось распарсить ответ
     */
    private ValuteDataXml parseXmlResponse(String xml) {

        try {
            JsonNode root = xmlMapper.readTree(xml);
            JsonNode valuteDataNode = root.path("Body")
                    .path("GetCursOnDateXMLResponse")
                    .path("GetCursOnDateXMLResult")
                    .path("ValuteData");

            ValuteDataXml valuteData = xmlMapper.treeToValue(valuteDataNode, ValuteDataXml.class);

            //Сервер возвращает название валюты с большим количество пробелов, убираем их
            for (ValuteRateXml valuteRateXml : valuteData.getValuteRates()) {
                String name = valuteRateXml.getName();
                valuteRateXml.setName(name.trim());
            }

            return valuteData;
        } catch (IOException e) {
            throw new ResponseParsingException("Ошибка парсинга ответа от центробанка", e);
        }
    }
}

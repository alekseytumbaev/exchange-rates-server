package com.example.exchangeratesserver.client.request;

import com.example.exchangeratesserver.client.exception.ResponseParsingException;
import com.example.exchangeratesserver.client.model.ValuteCode;
import com.example.exchangeratesserver.client.model.ValuteCursDynamicXml;
import com.example.exchangeratesserver.client.model.ValuteDynamicDataXml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Данные для запроса GetCursDynamicXML к ЦБ.
 * Запрос возвращает значения курса указанной валюты от и до указанных дат
 */
@UtilityClass
public class GetCursDynamicXmlRequest {
    public final String URI = "https://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
    public final HttpMethod METHOD = HttpMethod.POST;

    private final String BODY = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
              <soap12:Body>
                <GetCursDynamicXML xmlns="http://web.cbr.ru/">
                  <FromDate>%s</FromDate>
                  <ToDate>%s</ToDate>
                  <ValutaCode>%s</ValutaCode>
                </GetCursDynamicXML>
              </soap12:Body>
            </soap12:Envelope>
            """;

    private final String CONTENT_TYPE = "text/xml; charset=utf-8";

    public String getBody(ValuteCode code, LocalDate from, LocalDate to) {
        return String.format(BODY, from, to, code.getCentralBankCode());
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", CONTENT_TYPE);
        return headers;
    }

    public List<ValuteCursDynamicXml> parseResponse(String xml, XmlMapper mapper) {
        try {
            JsonNode root = mapper.readTree(xml);
            JsonNode valuteDataNode = root.path("Body")
                    .path("GetCursDynamicXMLResponse")
                    .path("GetCursDynamicXMLResult")
                    .path("ValuteData");

            ValuteDynamicDataXml valuteData = mapper.treeToValue(valuteDataNode, ValuteDynamicDataXml.class);
            return valuteData.getCursDynamics();
        } catch (IOException e) {
            throw new ResponseParsingException("Ошибка парсинга ответа от центробанка при запросе GetCursDynamicXML", e);
        }
    }
}

package com.example.exchangeratesserver.client.request;

import com.example.exchangeratesserver.client.exception.ResponseParsingException;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import com.example.exchangeratesserver.client.model.ValuteRateXml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Данные для запроса GetCursOnDateXML к ЦБ. Запрос возвращает курсы валют за указанную дату
 */
@UtilityClass
public class GetCursOnDateXmlRequest {
    public final String URI = "https://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
    public final HttpMethod METHOD = HttpMethod.POST;

    private final String BODY = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
              <soap12:Body>
                <GetCursOnDateXML xmlns="http://web.cbr.ru/">
                  <On_date>%s</On_date>
                </GetCursOnDateXML>
              </soap12:Body>
            </soap12:Envelope>""";

    private final String CONTENT_TYPE = "text/xml; charset=utf-8";

    public String getBody(LocalDate date) {
        return String.format(BODY, date);
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", CONTENT_TYPE);
        return headers;
    }

    /**
     * Функция для парсинга ответа центробанка по запросу GetCursOnDateXML
     *
     * @throws ResponseParsingException не удалось распарсить ответ
     */
    public ValuteDataXml parseResponse(String xml, XmlMapper mapper) throws ResponseParsingException {
        try {
            JsonNode root = mapper.readTree(xml);
            JsonNode valuteDataNode = root.path("Body")
                    .path("GetCursOnDateXMLResponse")
                    .path("GetCursOnDateXMLResult")
                    .path("ValuteData");

            ValuteDataXml valuteData = mapper.treeToValue(valuteDataNode, ValuteDataXml.class);

            //Сервер возвращает название валюты с большим количество пробелов, убираем их
            for (ValuteRateXml valuteRateXml : valuteData.getValuteRates()) {
                String name = valuteRateXml.getName();
                valuteRateXml.setName(name.trim());
            }

            return valuteData;
        } catch (IOException e) {
            throw new ResponseParsingException("Ошибка парсинга ответа от центробанка при запросе GetCursOnDateXML  ", e);
        }
    }
}

package com.example.exchangeratesserver.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ValuteData")
public class ValuteDataXml {

    @JacksonXmlElementWrapper(localName = "ValuteCurtsOnDateList", useWrapping = false)
    @JacksonXmlProperty(localName = "ValuteCursOnDate")
    private List<ValuteRateXml> valuteRates;

    @JacksonXmlProperty(localName = "OnDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
}

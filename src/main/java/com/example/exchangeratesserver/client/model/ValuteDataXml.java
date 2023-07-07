package com.example.exchangeratesserver.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ValuteData")
@AllArgsConstructor
@NoArgsConstructor
public class ValuteDataXml {

    @JacksonXmlElementWrapper(localName = "ValuteCurtsOnDateList", useWrapping = false)
    @JacksonXmlProperty(localName = "ValuteCursOnDate")
    private List<ValuteRateXml> valuteRates;

    @JacksonXmlProperty(localName = "OnDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
}

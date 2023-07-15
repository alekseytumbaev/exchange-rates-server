package com.example.exchangeratesserver.client.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JacksonXmlRootElement(localName = "ValuteData")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuteCursDynamicXml {

    @JacksonXmlProperty(localName = "CursDate")
    private OffsetDateTime cursDate;

    @JacksonXmlProperty(localName = "Vcode")
    private String centralBankCode;

    @JacksonXmlProperty(localName = "Vnom")
    private int nom;

    @JacksonXmlProperty(localName = "Vcurs")
    private BigDecimal curs;
}

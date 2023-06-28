package com.example.exchangeratesserver.client.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ValuteRateXml {

    @JacksonXmlProperty(localName = "Vname")
    private String name;

    @JacksonXmlProperty(localName = "Vnom")
    private Integer nom;

    @JacksonXmlProperty(localName = "Vcurs")
    private BigDecimal curs;

    @JacksonXmlProperty(localName = "Vcode")
    private Integer code;

    @JacksonXmlProperty(localName = "VchCode")
    private String chCode;
}

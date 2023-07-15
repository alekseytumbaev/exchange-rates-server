package com.example.exchangeratesserver.client.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JacksonXmlRootElement(localName = "ValuteData")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuteDynamicDataXml {

    @JacksonXmlElementWrapper(localName = "ValuteCursDynamicList", useWrapping = false)
    @JacksonXmlProperty(localName = "ValuteCursDynamic")
    private List<ValuteCursDynamicXml> cursDynamics;
}

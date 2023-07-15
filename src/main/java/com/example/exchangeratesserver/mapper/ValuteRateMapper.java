package com.example.exchangeratesserver.mapper;

import com.example.exchangeratesserver.client.model.ValuteCode;
import com.example.exchangeratesserver.client.model.ValuteCursDynamicXml;
import com.example.exchangeratesserver.client.model.ValuteRateXml;
import com.example.exchangeratesserver.model.ValuteRate;
import com.example.exchangeratesserver.model.ValuteRateDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.lang.Math.pow;

@UtilityClass
public class ValuteRateMapper {

    private final int FRACTION_LENGTH = 4; //количество цифр после запятой при переводе копеек в рубли

    public ValuteRateDto toDto(ValuteRate valuteRate) {
        return new ValuteRateDto(
                valuteRate.getDigitCode(),
                valuteRate.getChCode(),
                valuteRate.getValuteName(),
                valuteRate.getQuantity(),
                penniesToRubles(valuteRate.getRate()),
                valuteRate.getCurDate()
        );
    }

    /**
     * @param date дата за которую получена информация о курсе
     */
    public ValuteRate toEntity(ValuteRateXml valuteRateXml, LocalDate date) {
        return new ValuteRate(
                valuteRateXml.getCode(),
                valuteRateXml.getChCode(),
                valuteRateXml.getName(),
                valuteRateXml.getNom(),
                rublesToPennies(valuteRateXml.getCurs()),
                date
        );
    }

    public ValuteRateDto toDto(ValuteCursDynamicXml cursDynamic, ValuteCode code) {
        return new ValuteRateDto(
                code.getIsoCode(),
                code.toString(),
                code.getName(),
                cursDynamic.getNom(),
                cursDynamic.getCurs(),
                cursDynamic.getCursDate().toLocalDate()
        );
    }

    public BigDecimal penniesToRubles(int pennies) {
        BigDecimal penniesBd = BigDecimal.valueOf(pennies);
        BigDecimal divider = BigDecimal.valueOf(pow(10, FRACTION_LENGTH));
        return penniesBd.divide(divider, FRACTION_LENGTH, RoundingMode.FLOOR);
    }

    public int rublesToPennies(BigDecimal rubles) {
        BigDecimal factor = BigDecimal.valueOf(pow(10, FRACTION_LENGTH));
        return rubles.multiply(factor).intValue();
    }
}

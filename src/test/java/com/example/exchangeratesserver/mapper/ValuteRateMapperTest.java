package com.example.exchangeratesserver.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValuteRateMapperTest {

    @Test
    @DisplayName("Копейки должны конвертироваться в рубли c 4 знаками после запятой")
    void penniesToRublesTest() {
        BigDecimal rubles =  ValuteRateMapper.penniesToRubles(604181);
        assertEquals(BigDecimal.valueOf(60.4181), rubles);
    }

    @Test
    @DisplayName("Рубли должны конвертироваться в копейки")
    void rublesToPenniesTest() {
        int pennies = ValuteRateMapper.rublesToPennies(BigDecimal.valueOf(60.4181));
        assertEquals(604181, pennies);
    }
}
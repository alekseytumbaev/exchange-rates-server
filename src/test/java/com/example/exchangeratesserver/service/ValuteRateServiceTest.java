package com.example.exchangeratesserver.service;

import com.example.exchangeratesserver.client.CentralBankClient;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import com.example.exchangeratesserver.client.model.ValuteRateXml;
import com.example.exchangeratesserver.mapper.ValuteRateMapper;
import com.example.exchangeratesserver.model.ValuteRate;
import com.example.exchangeratesserver.model.ValuteRateDto;
import com.example.exchangeratesserver.repository.ValuteRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValuteRateServiceTest {

    @Mock
    private ValuteRateRepository rateRepo;

    @Mock
    private CentralBankClient cbClient;

    private ValuteRateService rateService;

    @BeforeEach
    void createService() {
        when(rateRepo.existsByCurDate(LocalDate.now())).thenReturn(true);
        rateService = new ValuteRateService(rateRepo, cbClient);
    }

    @Test
    @DisplayName("Должен попытаться найти отсутствующие данные в базе, запросить у клиента ЦБ и сохранить в базу")
    void findByCurDateNoDataInDbTest() {
        LocalDate date = LocalDate.now().minusDays(1);

        when(rateRepo.findByCurDate(date)).thenReturn(List.of());

        ValuteRateXml valuteRateXml = new ValuteRateXml("Доллар США", 1, BigDecimal.valueOf(30), 840, "USD");
        when(cbClient.getValuteData(date)).thenReturn(new ValuteDataXml(List.of(valuteRateXml), date));

        when(rateRepo.existsByCurDate(date)).thenReturn(false);

        ValuteRate valuteRate = ValuteRateMapper.toEntity(valuteRateXml, date);
        valuteRate.setId(1L);
        when(rateRepo.saveAll(any())).thenReturn(List.of(valuteRate));

        List<ValuteRateDto> rateDtos = rateService.findByCurDate(date);

        assertEquals(1, rateDtos.size());
        assertEquals(ValuteRateMapper.toDto(valuteRate), rateDtos.get(0));
    }

    @Test
    @DisplayName("Должен найти данные в базе и вернуть")
    void findByCurDateWithDataInDbTest() {
        LocalDate date = LocalDate.now().minusDays(1);

        BigDecimal rubles = BigDecimal.valueOf(30);
        ValuteRate rate = new ValuteRate(
                15, "USD", "Доллар США", 1,
                ValuteRateMapper.rublesToPennies(rubles), date
        );
        when(rateRepo.findByCurDate(date)).thenReturn(List.of(rate));

        List<ValuteRateDto> rateDtos = rateService.findByCurDate(date);
        assertEquals(1, rateDtos.size());
        assertEquals(ValuteRateMapper.toDto(rate), rateDtos.get(0));
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если курсы не отличаются")
    void updateRatesNoDifferenceTest() {
        LocalDate now = LocalDate.now();
        LocalDate lastUpdateDate = LocalDate.now().minusDays(1);
        ReflectionTestUtils.setField(rateService, "lastUpdateDate", lastUpdateDate);

        BigDecimal rubles = BigDecimal.valueOf(30);
        ValuteRateXml valuteRateXml = new ValuteRateXml("Доллар США", 1, rubles, 840, "USD");
        when(cbClient.getValuteData(now)).thenReturn(new ValuteDataXml(List.of(valuteRateXml), now));

        ValuteRate prevRate = ValuteRateMapper.toEntity(valuteRateXml, lastUpdateDate);
        when(rateRepo.findByCurDate(lastUpdateDate)).thenReturn(Arrays.asList(prevRate));

        ValuteRate newRate = ValuteRateMapper.toEntity(valuteRateXml, now);
        when(rateRepo.existsByCurDate(any())).thenReturn(false);
        when(rateRepo.saveAll(List.of(newRate))).thenReturn(Arrays.asList(newRate));

        assertEquals(0, rateService.updateRates().size(), "Курсы не отличаются, поэтому должен вернуть пустой список");
    }

    @Test
    @DisplayName("Должен вернуть отличающиеся курсы")
    void updateRatesWithDifferenceTest() {
        LocalDate now = LocalDate.now();
        LocalDate lastUpdateDate = LocalDate.now().minusDays(1);
        ReflectionTestUtils.setField(rateService, "lastUpdateDate", lastUpdateDate);

        ValuteRateXml usdXml = new ValuteRateXml("Доллар США", 1, BigDecimal.valueOf(90), 840, "USD");
        ValuteRateXml jpyXml = new ValuteRateXml("Японская иена", 100, BigDecimal.valueOf(63), 392, "JPY");

        when(cbClient.getValuteData(now)).thenReturn(new ValuteDataXml(List.of(usdXml, jpyXml), now));

        ValuteRate prevUsd = ValuteRateMapper.toEntity(usdXml, lastUpdateDate);
        prevUsd.setRate(ValuteRateMapper.rublesToPennies(BigDecimal.valueOf(30)));
        ValuteRate prevJpy = ValuteRateMapper.toEntity(jpyXml, lastUpdateDate);
        when(rateRepo.findByCurDate(lastUpdateDate)).thenReturn(Arrays.asList(prevUsd, prevJpy));

        ValuteRate newUsd = ValuteRateMapper.toEntity(usdXml, now);
        ValuteRate newJpy = ValuteRateMapper.toEntity(jpyXml, now);
        when(rateRepo.existsByCurDate(any())).thenReturn(false);
        when(rateRepo.saveAll(List.of(newUsd, newJpy))).thenReturn(Arrays.asList(newUsd, newJpy));

        List<ValuteRateDto> rateDtos = rateService.updateRates();
        assertEquals(1, rateDtos.size(), "Курсы отличаются, размер списка должен быть 1");
        assertEquals(ValuteRateMapper.toDto(newUsd), rateDtos.get(0), "В списке должен быть новый курс доллара");
    }

}
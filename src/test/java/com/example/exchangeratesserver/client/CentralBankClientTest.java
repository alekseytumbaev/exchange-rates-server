package com.example.exchangeratesserver.client;

import com.example.exchangeratesserver.client.exception.FutureDateRequestException;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class CentralBankClientTest {

    @Autowired
    private CentralBankClient centralBankClient;

    @Test
    @DisplayName("Получить курс валюты за текущую дату")
    void getValuteData() {
        LocalDate date = LocalDate.now();
        ValuteDataXml valuteData = centralBankClient.getValuteData(date);
        LocalDate valuteDate = valuteData.getDate();
        assertTrue(
                valuteDate.isEqual(date) || valuteDate.isBefore(date),
                "Получены курсы за дату в будущем, должны быть за текущую или прошедшую дату"
        );
        assertFalse(valuteData.getValuteRates().isEmpty(),
                "Список курсов не должен быть пустым"
        );
    }

    @Test
    @DisplayName("Исключение, если дата указана в будущем")
    void getValuteDataInFutureThrowExcpetion() {
        assertThrows(
                FutureDateRequestException.class,
                () -> centralBankClient.getValuteData(LocalDate.now().plusDays(1)),
                "Должно быть исключение, если дата указана в будущем"
        );
    }
}
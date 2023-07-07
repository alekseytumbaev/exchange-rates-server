package com.example.exchangeratesserver.service;

import com.example.exchangeratesserver.client.CentralBankClient;
import com.example.exchangeratesserver.client.exception.FutureDateRequestException;
import com.example.exchangeratesserver.client.model.ValuteDataXml;
import com.example.exchangeratesserver.exception.FutureDateSavingException;
import com.example.exchangeratesserver.exception.RatesAlreadyExistForDateException;
import com.example.exchangeratesserver.mapper.ValuteRateMapper;
import com.example.exchangeratesserver.model.ValuteRate;
import com.example.exchangeratesserver.model.ValuteRateDto;
import com.example.exchangeratesserver.repository.ValuteRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;

@Service
@Transactional
public class ValuteRateService {
    private LocalDate lastUpdateDate;

    private final ValuteRateRepository repo;
    private final CentralBankClient cbClient;

    public ValuteRateService(ValuteRateRepository repo, CentralBankClient cbClient) {
        this.repo = repo;
        this.cbClient = cbClient;

        //инициализация даты последнего обновления
        LocalDate now = LocalDate.now();
        if (!repo.existsByCurDate(now)) {
            repo.saveAll(getRatesFromCentralBank(now));
        }
        lastUpdateDate = now;
    }

    /**
     * Находит курсы по заданной дате, если курсов нет в базе, запрашивает у центробанка и сохраняет в бд
     */
    public List<ValuteRateDto> findByCurDate(LocalDate date) {
        List<ValuteRate> rates = repo.findByCurDate(date);
        if (rates.isEmpty()) {
            rates = saveAndUpdateLastDate(getRatesFromCentralBank(date));
        }
        return rates.stream().map(ValuteRateMapper::toDto).toList();
    }

    /**
     * Проверяет есть ли новые данные по курсам, если есть - сохраняет в базу
     *
     * @return курсы, значения которых отличаются от предыдущих, пустой список, если не отличаются
     */
    public List<ValuteRateDto> updateRates() {
        LocalDate now = LocalDate.now();
        //Если дата еще не обновилась, значит и курс тоже
        if (lastUpdateDate.isEqual(now)) {
            return new ArrayList<>();
        }

        List<ValuteRate> previousRates = repo.findByCurDate(lastUpdateDate);
        List<ValuteRate> newRates = saveAndUpdateLastDate(getRatesFromCentralBank(now));

        List<ValuteRate> differences = findDifferences(previousRates, newRates);
        return differences.stream().map(ValuteRateMapper::toDto).toList();
    }

    /**
     * Находит отличающиеся курсы, сортирует списки по коду валюты для ускорения поиска
     *
     * @return Курсы из newRates, отличающиеся от курсов в previousRate, либо отсутствующие там
     */
    private List<ValuteRate> findDifferences(List<ValuteRate> previousRates, List<ValuteRate> newRates) {
        Comparator<ValuteRate> comparator = Comparator.comparingInt(ValuteRate::getDigitCode);
        previousRates.sort(comparator);
        newRates.sort(comparator);

        List<ValuteRate> differences = new ArrayList<>();
        for (int i = 0; i < previousRates.size(); i++) {
            if (previousRates.get(i).getRate() != newRates.get(i).getRate()) {
                differences.add(newRates.get(i));
            }
        }

        //если появились новые валюты, добавляем их в изменения
        if (previousRates.size() < newRates.size()) {
            for (int i = previousRates.size(); i < newRates.size(); i++) {
                differences.add(newRates.get(i));
            }
        }
        return differences;
    }

    /**
     * Получает данные от клиента центробанка и преобразует в список {@link ValuteRate}
     *
     * @param date дата за которую нужно получить курсы
     * @return Список курсов с указанной датой.
     * Не зависимо от того за какую дату пришли курсы, в ответных данных будет установлена переданная дата.
     * @throws FutureDateRequestException                                                дата указана в будущем
     * @throws org.springframework.web.client.RestClientException                        ошибка при отправке запроса центробанку
     * @throws com.example.exchangeratesserver.client.exception.ResponseParsingException не удалось распарсить ответ
     */
    private List<ValuteRate> getRatesFromCentralBank(LocalDate date) {
        ValuteDataXml valuteDataXml = cbClient.getValuteData(date);
        return valuteDataXml.getValuteRates()
                .stream().map(vr -> ValuteRateMapper.toEntity(vr, date)).toList();
    }

    /**
     * Сохраняет курсы и, если их дата позже даты последнего обновления,
     * устанавливает дату курсов как дату последнего обновления.
     *
     * @throws RatesAlreadyExistForDateException курсы на эту дату уже есть
     * @throws FutureDateSavingException         дата курсов в будущем
     */
    private List<ValuteRate> saveAndUpdateLastDate(List<ValuteRate> rates) {
        if (rates.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate ratesDate = rates.get(0).getCurDate();

        if (ratesDate.isAfter(LocalDate.now())) {
            throw new FutureDateSavingException("Невозможно сохранить курсы за дату в будущем: " + ratesDate);
        }
        if (repo.existsByCurDate(ratesDate)) {
            throw new RatesAlreadyExistForDateException(format("Курсы за дату %s уже сохранены", ratesDate));
        }

        if (ratesDate.isAfter(lastUpdateDate)) {
            lastUpdateDate = ratesDate;
        }

        return repo.saveAll(rates);
    }
}

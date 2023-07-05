package com.example.exchangeratesserver.service;

import com.example.exchangeratesserver.model.ValuteRate;
import com.example.exchangeratesserver.repository.ValuteRateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ValuteRateService {
    private final ValuteRateRepository repo;
    public ValuteRateService(ValuteRateRepository repo) {
        this.repo = repo;
    }

    // Получить записи по заданной дате
    public List<ValuteRate> findByCurDate(LocalDate date)
    {
        return repo.findByCurDate(date);
    }
}

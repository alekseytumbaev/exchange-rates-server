package com.example.exchangeratesserver.repository;
import com.example.exchangeratesserver.model.ValuteRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ValuteRateRepository extends JpaRepository<ValuteRate,Long> {
    public List<ValuteRate> findByCurDate(LocalDate date); // Получить записи по заданной дате
}

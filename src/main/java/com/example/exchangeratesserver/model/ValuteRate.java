package com.example.exchangeratesserver.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "valuterate")
@Data              // Добавить сеттеры геттеры
@NoArgsConstructor // Добавить конструктор без аргументов
public class ValuteRate {
    public ValuteRate(int digitCode, String chCode, String valuteName, int quantity, int rate, LocalDate curDate) {
        this.digitCode = digitCode;
        this.setChCode(chCode);    // Обрезают до нужной длины
        this.setValuteName(valuteName);//
        this.quantity = quantity;
        this.rate = rate;
        this.curDate = curDate;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "digitcode")
    private int digitCode;
    @Column(name = "chcode")
    private String chCode;
    @Column(name = "valutename")
    private String valuteName;
    @Column(name = "quantity")
    private int quantity;

    /**
     * Значение хранится в копейках
     */
    @Column(name = "rate")
    private int rate;

    @Column(name = "curdate")
    private LocalDate curDate;
    private static final int MAX_CH_CODE = 20;      // Максимальная длина буквенного кода
    private static final int MAX_VALUTE_NAME = 150; // Максимальная длина названия валюты

    void setChCode(String chCode) // Чтобы не ввели больше символов, чем выделено в бд
    {
        if (chCode.length() <= MAX_CH_CODE) this.chCode = chCode;
        else this.chCode = chCode.substring(0, MAX_CH_CODE); // Обрезать до 20 символов
    }

    void setValuteName(String valuteName) {
        if (valuteName.length() <= MAX_VALUTE_NAME) this.valuteName = valuteName;
        else this.valuteName = valuteName.substring(0, MAX_VALUTE_NAME); // Обрезать до 150 символов
    }
}
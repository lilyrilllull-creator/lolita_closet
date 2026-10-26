package com.example.lolitaCloset.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "series")
@Data
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seriesId;

    // ブランド（親）と紐付けるためのIDの箱
    private Long brandId;

    private String seriesName;
}


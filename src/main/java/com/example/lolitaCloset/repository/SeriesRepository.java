package com.example.lolitaCloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.Series;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
}

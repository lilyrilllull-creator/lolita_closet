package com.example.lolitaCloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
	boolean existsByColorName(String colorName);
}

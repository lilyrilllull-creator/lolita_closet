package com.example.lolitaCloset.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
	boolean existsByBrandName(String brandName);
	Optional<Brand> findByBrandName(String brandName);
}

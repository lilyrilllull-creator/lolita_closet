package com.example.lolitaCloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.MainCategory;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
	 boolean existsByMainCategoryName(String mainCategoryName);
	 java.util.Optional<MainCategory> findByMainCategoryName(String mainCategoryName);
}

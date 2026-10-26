package com.example.lolitaCloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.PurchaseRoute;

@Repository
public interface PurchaseRouteRepository extends JpaRepository<PurchaseRoute, Long> {
}


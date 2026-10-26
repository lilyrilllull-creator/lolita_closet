package com.example.lolitaCloset.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "purchase_routes")
@Data
public class PurchaseRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseRouteId;

    // ブランド限定経路の場合に、ブランドと紐付けるための箱（空欄OK）
    private Long brandId;

    private String routeName;

    //  trueなら全ブランド共通で表示するフラグ
    private Boolean isCommon;
}

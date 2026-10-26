package com.example.lolitaCloset.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String itemName;

    // ブランド判別、必須項目
    // ★マスタ結合の魔法：ブランドIDだけでなく、ブランドの情報（名前など）を丸ごとガッチャンコする！
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private Brand brand;

    // 画面からの入力（登録）用に、IDを入れる箱もそのまま残しておきます
    @jakarta.persistence.Column(name = "brand_id")
    private Long brandId;


    // 小分類IDのみを保持
    private Long subCategoryId;

    private Long colorId;

    // ポップアップ連動マスタ（空欄・NULLを許可）
    private Long seriesId;
    private Long purchaseRouteId;

    private String formalColorName;
    private String sizeName;

    // 寸法
    private Integer lengthCm;
    private Integer bustCm;
    private Integer waistCm;

    // 型番
    private String itemCode;
    
    // 購入日
    private LocalDate purchaseDate; 

    // 1:新品、2:中古
    private Integer statusCode;

    // 価格周り
    private Integer purchasePrice; // 購入価格
    private Integer listPrice; // 定価(税込み)
    
    private String memo;

    // 削除フラグ
    private Boolean deleteFlag;

    // 業務必須：システム側で自動で日付が入るタイムスタンプの箱
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // 🌸【これだけ追加！】MySQLからお写真を引っ張ってくる際、
    // あなたが画面で「選択した順番（＝image_idが登録された順）」で確実に1列に並ぶように強制固定します！
    @jakarta.persistence.OneToMany(mappedBy = "itemId")
    @jakarta.persistence.OrderBy("imageId ASC")
    private java.util.List<ItemImage> images;
    
    // 🌸【完全修正】MySQLに増やしたuser_id列と紐づく、本物の受け皿フィールドをJPAに定義します
    @jakarta.persistence.Column(name = "user_id")
    private Long userId;




}

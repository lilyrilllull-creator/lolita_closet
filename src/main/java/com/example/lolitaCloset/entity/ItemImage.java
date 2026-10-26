package com.example.lolitaCloset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "item_images")
@Data
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "item_id")
    private Long itemId;

    // 🌸 解決の鍵：あなたの作った本物の列名「image_url」を指定して、Javaの変数「imagePath」とカチッと接着します！
    @Column(name = "image_url")
    private String imagePath;

    @Column(name = "is_main")
    private Boolean isMain;
}

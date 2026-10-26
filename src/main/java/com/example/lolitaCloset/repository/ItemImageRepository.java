package com.example.lolitaCloset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    // お洋服のIDを指定して、その服に紐づくすべての画像をセットで持ってくる魔法
    List<ItemImage> findByItemId(Long itemId);
}

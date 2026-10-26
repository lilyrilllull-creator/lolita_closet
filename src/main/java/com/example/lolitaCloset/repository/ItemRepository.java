package com.example.lolitaCloset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Item> {

	Page<Item> findByUserIdAndDeleteFlagFalseOrderByCreatedAtAsc(Long userId, Pageable pageable);
	
    // 🌸 1. 検索ワードがない場合（全件表示 ＋ 50件制限用）
    Page<Item> findByDeleteFlagFalseOrderByCreatedAtAsc(Pageable pageable);
    Page<Item> findByDeleteFlagFalseOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseOrderByPurchasePriceAsc(Pageable pageable);
    Page<Item> findByDeleteFlagFalseOrderByPurchasePriceDesc(Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseOrderByPurchaseDateAsc(Pageable pageable);
    Page<Item> findByDeleteFlagFalseOrderByPurchaseDateDesc(Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseOrderByBrandIdAsc(Pageable pageable);
    Page<Item> findByDeleteFlagFalseOrderByBrandIdDesc(Pageable pageable);

    // 🌸 2. 検索キーワードがある場合（商品名部分一致 ＋ 50件制限用）
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByCreatedAtAsc(String keyword, Pageable pageable);
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByPurchasePriceAsc(String keyword, Pageable pageable);
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByPurchasePriceDesc(String keyword, Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByPurchaseDateAsc(String keyword, Pageable pageable);
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByPurchaseDateDesc(String keyword, Pageable pageable);
    
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByBrandIdAsc(String keyword, Pageable pageable);
    Page<Item> findByDeleteFlagFalseAndItemNameContainingOrderByBrandIdDesc(String keyword, Pageable pageable);
    
    // 🌸 クローゼットに眠るお洋服の総着数を安全にカウントするメソッド
    long countByDeleteFlagFalse();

}

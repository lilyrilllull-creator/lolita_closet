package com.example.lolitaCloset.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lolitaCloset.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 🌸 ログイン時に入力された「ユーザー名」から、該当するユーザーアカウント情報を安全に1件逆引きする魔法のメソッド
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 🌸 新規登録時に、同じユーザー名がすでに誰かに使われていないかを瞬時にチェックする安全弁
     */
    boolean existsByUsername(String username);
}

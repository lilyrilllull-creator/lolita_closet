package com.example.lolitaCloset.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.lolitaCloset.entity.User;
import com.example.lolitaCloset.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🌸 法律（SecurityConfig）で定義した暗号化パーツとリポジトリをカチッと合流
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 🏰 新しいユーザーを安全に暗号化してMySQLに登録するメソッド
     * @return エラーメッセージ（成功時は null）
     */
    public String registerNewUser(String username, String rawPassword) {
        
        // 1. 同一ユーザー名の重複チェック（安全弁）
        if (userRepository.existsByUsername(username)) {
            return "🎀 お嬢様、ごめんなさい……！そのユーザー名はすでに他の方が使われているみたい。";
        }

        // 2. 新しいユーザーエンティティの作成
        User newUser = new User();
        newUser.setUsername(username);
        
        // 🌟 生のパスワードを、セキュリティConfigと同じBCryptで100%確実に暗号化して格納！
        newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        newUser.setRole("ROLE_USER"); // 標準権限を付与

        // 3. MySQLへ保存
        userRepository.save(newUser);
        return null; // エラーなし（成功）
    }
}

package com.example.lolitaCloset.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.lolitaCloset.entity.User;
import com.example.lolitaCloset.repository.UserRepository;

/**
 * 🏰 Spring Securityの認証機構とMySQLのユーザーマスタをガチッと繋ぐ心臓部サービス
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 🌸 優秀なLombokのおかげで、コンストラクタはJavaが裏側で自動生成してインジェクションしてくれます
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 🔑 ログイン画面からユーザー名が送られてきた瞬間に、JPAを介してMySQLを自動検索する義務メソッド
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. 入力されたユーザー名でMySQLのusersテーブルを狙い撃ち検索
        User user = userRepository.findByUsername(username)
                // 万が一、そんな名前のユーザーが登録されていなければ「存在しません」と安全に例外を投げて突き放します
                .orElseThrow(() -> new UsernameNotFoundException("お嬢様、入力されたユーザー名（" + username + "）は見つかりませんわ。"));

        // 2. ユーザーが見つかったら、Spring Securityが読める「専用の証明書パック」にトランスフォームします
        // 🌸 3. ユーザーが持っている権限（role）を、Spring Securityが認識できる権限パーツ（Authority）に変換
        String userRole = (user.getRole() != null) ? user.getRole() : "ROLE_USER";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole);

        // 🌸 4. 【大本命の証明書発行】
        // Spring Security標準の「User」オブジェクトに、私たちのエンティティの情報をカチッと詰め込んで返却します！
        // これを受け取ったSpring Securityが、裏側で入力されたパスワードと暗号の照合を全自動で執り行います。
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}

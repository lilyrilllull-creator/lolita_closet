package com.example.lolitaCloset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 🏰 複数ユーザーの認証情報を安全に管理するためのユーザーエンティティクラス
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // ユーザー名（重複を絶対に許さないUNIQUE制約をマッピング）
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    // 強力に暗号化（ハッシュ化）されたパスワードを格納するフィールド
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // ユーザーの権限（一般ユーザー：ROLE_USER, 管理者：ROLE_ADMIN など）
    @Column(name = "role", length = 20)
    private String role;
}
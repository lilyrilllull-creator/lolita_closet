package com.example.lolitaCloset.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lolitaCloset.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;

    // 🌸 新規登録用サービスをインジェクション
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 🏰 新規ユーザー登録画面を表示するエンドポイント
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "user-register"; // templates/register.html を開きます
    }

    /**
     * 💌 新規登録ボタンが押された時のポスト送信を受け止める処理
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model) {
        
        // サービスを呼び出して登録処理を実行
        String errorMessage = userService.registerNewUser(username, password);
        
        if (errorMessage != null) {
            // 重複エラーなどがあった場合は、画面に警告を乗せて登録画面へ戻します
            model.addAttribute("error", errorMessage);
            return "register";
        }
        
        // 登録が完全に大成功したら、ログイン画面へ「?success」を付けてリダイレクト！
        return "redirect:/login?success";
    }
}

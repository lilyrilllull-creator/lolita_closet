package com.example.lolitaCloset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// 🌸【完全修正】私たちの独自クラス名ではなく、Spring公式の「UserDetailsService」インターフェースの型で受け取ります！
	private final UserDetailsService userDetailsService;

	// 認証サービスをコンストラクタで安全にインジェクションします
	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * 👑 ユーザー検索サービスと暗号化パーツを「これのペアで使え！」とマネージャーに強制バインドします
	 */
	@Bean
	public org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider() {
		// 🌸【完全・最終修正】クイックフィックスの指示通り、newする瞬間に引数として「userDetailsService」を直接放り込みます！
		org.springframework.security.authentication.dao.DaoAuthenticationProvider authProvider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider(
				userDetailsService);

		// パスワードの暗号化設定だけをセットします
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * 🌸 1. セキュリティの防壁ルール（法律）を細かくカスタマイズする主役メソッド
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// ① 【URLの閲覧制限ルール】
				.authorizeHttpRequests(auth -> auth
						// 🏰 ログイン画面、静的ファイルは「誰でも見られる」ように大解放！
						.requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/uploads/**")
						.permitAll()
						// 👗 それ以外の一覧画面などはログイン必須に
						.anyRequest().authenticated())

				// ② 【自作のログイン画面への配線設定】
				.formLogin(form -> form
						.loginPage("/login")
						// 🌸【変更】ログイン成功時に、自動的に「ダッシュボード（トップ画面）」へパッとジャンプさせます！
						.defaultSuccessUrl("/", true) // ※もしダッシュボードのURLが /dashboard の場合は "/" を "/dashboard" に変えてください
						.loginProcessingUrl("/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.permitAll())

				// ③ 【ログアウト時の挙動設定】
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll());

		// 作成した照合プロバイダーをここにガチッとドッキング
		http.authenticationProvider(authenticationProvider());

		return http.build();
	}

	/**
	 * 🌸 2. パスワードを強力に暗号化（ハッシュ化）する魔法のパーツ
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

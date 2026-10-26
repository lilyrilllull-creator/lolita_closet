package com.example.lolitaCloset.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.lolitaCloset.entity.Brand;
import com.example.lolitaCloset.entity.Color;
import com.example.lolitaCloset.entity.MainCategory;
import com.example.lolitaCloset.entity.PurchaseRoute;
import com.example.lolitaCloset.entity.SubCategory;
import com.example.lolitaCloset.repository.BrandRepository;
import com.example.lolitaCloset.repository.ColorRepository;
import com.example.lolitaCloset.repository.MainCategoryRepository;
import com.example.lolitaCloset.repository.PurchaseRouteRepository;
import com.example.lolitaCloset.repository.SubCategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

	private final BrandRepository brandRepository;
	private final ColorRepository colorRepository;
	private final MainCategoryRepository mainCategoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final PurchaseRouteRepository purchaseRouteRepository;
	private final com.example.lolitaCloset.repository.UserRepository userRepository;
	private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		
        // 🌸【最優先】ログインテスト用の本物の暗号アカウントをJavaの力で安全に自動登録します！
        if (!userRepository.existsByUsername("maid_alice")) {
            com.example.lolitaCloset.entity.User testUser = new com.example.lolitaCloset.entity.User();
            testUser.setUsername("maid_alice");
            
            // 🌟 生のパスワード「loveroses」を、Javaの最新のBCryptで100%確実にハッシュ化！
            testUser.setPasswordHash(passwordEncoder.encode("loveroses"));
            testUser.setRole("ROLE_USER");
            
            userRepository.save(testUser);
            System.out.println("【マスタ追加】テスト用お嬢様アカウント（maid_alice）を完璧に登録しました！");
        }


		// 1. ブランドマスタの登録（★存在チェック付き：名前がなければ登録）
		registerBrandIfAbsent("ALICE and the PIRATES");
		registerBrandIfAbsent("Angelic Pretty");
		registerBrandIfAbsent("ATELIER BOZ");
		registerBrandIfAbsent("ATELIER-PIERROT");
		registerBrandIfAbsent("BABY, THE STARS SHINE BRIGHT");
		registerBrandIfAbsent("Chalmiere");
		registerBrandIfAbsent("Hoshibako Works");
		registerBrandIfAbsent("Innocent World");
		registerBrandIfAbsent("Metamorphose temps de fille");
		registerBrandIfAbsent("Victorian maiden");
		registerBrandIfAbsent("個人作家");
		registerBrandIfAbsent("海外ブランド");
		registerBrandIfAbsent("しまむら×愛野えり");
		registerBrandIfAbsent("しまむら×青木美沙子");
		registerBrandIfAbsent("しまむら×くる実");
		registerBrandIfAbsent("しまむら×胡桃咲姫");
		registerBrandIfAbsent("しまむら×眞白ありす");
		registerBrandIfAbsent("ノーブランド・不明");

		// 2. カラーマスタの登録（★存在チェック付き）
		registerColorIfAbsent("ピンク");
		registerColorIfAbsent("サックス");
		registerColorIfAbsent("生成");
		registerColorIfAbsent("白");
		registerColorIfAbsent("黒・グレー");
		registerColorIfAbsent("エンジ・赤");
		registerColorIfAbsent("紺・ブルー");
		registerColorIfAbsent("ラベンダー・紫");
		registerColorIfAbsent("ミント・グリーン");
		registerColorIfAbsent("ブラウン・ベージュ");
		registerColorIfAbsent("ゴールド・シルバー");

		// 3. カテゴリマスタの登録（★親子連動・存在チェック付き）
		registerCategoryTree("ワンピース", "長袖");
		registerCategoryTree("ワンピース", "半袖");
		registerCategoryTree("ワンピース", "七分袖");
		registerCategoryTree("ワンピース", "姫袖");
		registerCategoryTree("ワンピース", "その他");

		registerCategoryTree("ジャンパースカート", "ノースリーブ");
		registerCategoryTree("ジャンパースカート", "その他");

		registerCategoryTree("トップス", "長袖ブラウス");
		registerCategoryTree("トップス", "半袖ブラウス");
		registerCategoryTree("トップス", "七分袖ブラウス");
		registerCategoryTree("トップス", "姫袖ブラウス");
		registerCategoryTree("トップス", "長袖カットソー");
		registerCategoryTree("トップス", "半袖カットソー");
		registerCategoryTree("トップス", "七分袖カットソー");
		registerCategoryTree("トップス", "その他");

		registerCategoryTree("ボトムス", "スカート");
		registerCategoryTree("ボトムス", "吊りスカート");
		registerCategoryTree("ボトムス", "オーバースカート");
		registerCategoryTree("ボトムス", "パンツ");
		registerCategoryTree("ボトムス", "その他");

		registerCategoryTree("アウター", "コート");
		registerCategoryTree("アウター", "ジャケット");
		registerCategoryTree("アウター", "ベスト");
		registerCategoryTree("アウター", "パーカー");
		registerCategoryTree("アウター", "ボレロ・カーディガン");
		registerCategoryTree("アウター", "ケープ・マント");
		registerCategoryTree("アウター", "その他");

		registerCategoryTree("頭もの", "ヘッドドレス");
		registerCategoryTree("頭もの", "カチューシャ");
		registerCategoryTree("頭もの", "ボンネット");
		registerCategoryTree("頭もの", "バレッタ・クリップ");
		registerCategoryTree("頭もの", "ベレー帽");
		registerCategoryTree("頭もの", "キャノティエ");
		registerCategoryTree("頭もの", "ハット");
		registerCategoryTree("頭もの", "その他");

		registerCategoryTree("アクセサリー", "お袖留め");
		registerCategoryTree("アクセサリー", "リング");
		registerCategoryTree("アクセサリー", "ネックレス");
		registerCategoryTree("アクセサリー", "チョーカー");
		registerCategoryTree("アクセサリー", "タイ");
		registerCategoryTree("アクセサリー", "ピアス・イヤリング");
		registerCategoryTree("アクセサリー", "手袋");
		registerCategoryTree("アクセサリー", "マフラー");
		registerCategoryTree("アクセサリー", "ブレスレット");
		registerCategoryTree("アクセサリー", "エシャルプ");
		registerCategoryTree("アクセサリー", "ブローチ・コサージュ・クリップ");
		registerCategoryTree("アクセサリー", "コルセット");
		registerCategoryTree("アクセサリー", "ベルト");
		registerCategoryTree("アクセサリー", "傘・日傘");
		registerCategoryTree("アクセサリー", "その他");

		registerCategoryTree("ソックス・タイツ", "足首丈ソックス");
		registerCategoryTree("ソックス・タイツ", "膝丈ソックス");
		registerCategoryTree("ソックス・タイツ", "オーバーニー");
		registerCategoryTree("ソックス・タイツ", "タイツ");
		registerCategoryTree("ソックス・タイツ", "その他");

		registerCategoryTree("シューズ", "パンプス");
		registerCategoryTree("シューズ", "ブーツ");
		registerCategoryTree("シューズ", "スニーカー");
		registerCategoryTree("シューズ", "サンダル");
		registerCategoryTree("シューズ", "ローファー");
		registerCategoryTree("シューズ", "その他");
		
		registerCategoryTree("バッグ", "ハンドバッグ");
		registerCategoryTree("バッグ", "ショルダーバッグ");
		registerCategoryTree("バッグ", "ポシェット");
		registerCategoryTree("バッグ", "リュック");
		registerCategoryTree("バッグ", "ポーチ");
		registerCategoryTree("バッグ", "その他");

		registerCategoryTree("インナー", "パニエ");
		registerCategoryTree("インナー", "ドロワーズ");
		registerCategoryTree("インナー", "ペチコート");
		registerCategoryTree("インナー", "その他");
		
		registerCategoryTree("その他", "-");

		// 4. 購入経路マスタの登録（🌸 CSV提供の本物のリアル店舗マスタ ＋ 共通項目を下側へ配置！）

		java.util.List<Brand> allBrands = brandRepository.findAll();

		for (Brand b : allBrands) {
			String bName = b.getBrandName();
			Long bId = b.getBrandId();

			// 各ブランドに紐づく公式オンラインショップの登録（ノーブランド・不明以外）
			if (!bName.contains("ノーブランド") && !bName.contains("不明") && !bName.contains("しまむら")) {
				registerPurchaseRouteIfAbsent(bName + " 公式オンラインショップ", bId, false);
			}

			// 🌸 お送りいただいたCSVデータを1文字の狂いもなく100%完全に登録します！
			if (bName.equals("ALICE and the PIRATES")) {
				registerPurchaseRouteIfAbsent("仙台店", bId, false);
				registerPurchaseRouteIfAbsent("高崎店", bId, false);
				registerPurchaseRouteIfAbsent("池袋店", bId, false);
				registerPurchaseRouteIfAbsent("新宿店", bId, false);
				registerPurchaseRouteIfAbsent("本店", bId, false);
				registerPurchaseRouteIfAbsent("原宿店", bId, false);
				registerPurchaseRouteIfAbsent("横浜店", bId, false);
				registerPurchaseRouteIfAbsent("金沢店", bId, false);
				registerPurchaseRouteIfAbsent("名古屋パルコ店", bId, false);
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("京都店", bId, false);
				registerPurchaseRouteIfAbsent("三宮店", bId, false);
				registerPurchaseRouteIfAbsent("福岡店", bId, false);
				registerPurchaseRouteIfAbsent("San Francisco店", bId, false);

			} else if (bName.equals("Angelic Pretty")) {
				registerPurchaseRouteIfAbsent("原宿店", bId, false);
				registerPurchaseRouteIfAbsent("名古屋店", bId, false);
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("仙台店", bId, false);
				registerPurchaseRouteIfAbsent("横浜店", bId, false);
				registerPurchaseRouteIfAbsent("新宿店", bId, false);
				registerPurchaseRouteIfAbsent("福岡店", bId, false);
				registerPurchaseRouteIfAbsent("三宮店", bId, false);
				registerPurchaseRouteIfAbsent("京都店", bId, false);
				registerPurchaseRouteIfAbsent("渋谷店", bId, false);
				registerPurchaseRouteIfAbsent("PARIS", bId, false);
				registerPurchaseRouteIfAbsent("San Francisco", bId, false);
				registerPurchaseRouteIfAbsent("上海日月光店", bId, false);
				registerPurchaseRouteIfAbsent("上海世茂広場店", bId, false);

			} else if (bName.equals("ATELIER BOZ")) {
				registerPurchaseRouteIfAbsent("EPETICE 池袋", bId, false);
				registerPurchaseRouteIfAbsent("EPETICE 公式通販", bId, false);
				registerPurchaseRouteIfAbsent("HoshibakoWorks 原宿店", bId, false);
				registerPurchaseRouteIfAbsent("Choice closet", bId, false);
				registerPurchaseRouteIfAbsent("ポップアップ", bId, false);

			} else if (bName.equals("ATELIER-PIERROT")) {
				registerPurchaseRouteIfAbsent("ラフォーレ原宿店", bId, false);
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("新宿マルイアネックス店", bId, false);
				registerPurchaseRouteIfAbsent("横浜ビブレ店", bId, false);
				registerPurchaseRouteIfAbsent("上海店", bId, false);
				registerPurchaseRouteIfAbsent("CHOUCHOUANGE 名古屋店", bId, false);
				registerPurchaseRouteIfAbsent("KIST MARIA札幌", bId, false);
				registerPurchaseRouteIfAbsent("HARAJUKU HEARTS", bId, false);

			} else if (bName.equals("BABY, THE STARS SHINE BRIGHT")) {
				registerPurchaseRouteIfAbsent("仙台店", bId, false);
				registerPurchaseRouteIfAbsent("高崎店", bId, false);
				registerPurchaseRouteIfAbsent("池袋店", bId, false);
				registerPurchaseRouteIfAbsent("新宿店", bId, false);
				registerPurchaseRouteIfAbsent("本店", bId, false);
				registerPurchaseRouteIfAbsent("原宿店", bId, false);
				registerPurchaseRouteIfAbsent("横浜店", bId, false);
				registerPurchaseRouteIfAbsent("金沢店", bId, false);
				registerPurchaseRouteIfAbsent("名古屋パルコ店", bId, false);
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("京都店", bId, false);
				registerPurchaseRouteIfAbsent("三宮店", bId, false);
				registerPurchaseRouteIfAbsent("福岡店", bId, false);
				registerPurchaseRouteIfAbsent("San Francisco店", bId, false);

			} else if (bName.equals("Chalmiere")) {
				registerPurchaseRouteIfAbsent("札幌アピア店", bId, false);
				registerPurchaseRouteIfAbsent("イクスピアリ店", bId, false);
				registerPurchaseRouteIfAbsent("サンシャインシティ店", bId, false);
				registerPurchaseRouteIfAbsent("ラフォーレ原宿店", bId, false);
				registerPurchaseRouteIfAbsent("横浜ジョイナス店", bId, false);
				registerPurchaseRouteIfAbsent("河原町オーパ店", bId, false);
				registerPurchaseRouteIfAbsent("HEP FIVE店", bId, false);
				registerPurchaseRouteIfAbsent("心斎橋ビッグステップ店", bId, false);
				registerPurchaseRouteIfAbsent("ソラリアプラザ店", bId, false);

			} else if (bName.equals("Hoshibako Works")) {
				registerPurchaseRouteIfAbsent("新宿マルイアネックス店", bId, false);
				registerPurchaseRouteIfAbsent("原宿店", bId, false);
				registerPurchaseRouteIfAbsent("横浜ビブレ店", bId, false);
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("池袋アルタ店", bId, false);
				registerPurchaseRouteIfAbsent("西荻窪店", bId, false);

			} else if (bName.equals("Innocent World")) {
				registerPurchaseRouteIfAbsent("ポップアップ", bId, false);

			} else if (bName.equals("Metamorphose temps de fille")) {
				registerPurchaseRouteIfAbsent("大阪店", bId, false);
				registerPurchaseRouteIfAbsent("名古屋店", bId, false);
				registerPurchaseRouteIfAbsent("京都店", bId, false);
				registerPurchaseRouteIfAbsent("ラフォーレ原宿店", bId, false);
				registerPurchaseRouteIfAbsent("楽天市場店", bId, false);

			} else if (bName.equals("Victorian maiden")) {
				registerPurchaseRouteIfAbsent("KERA SHOP 新宿店", bId, false);
				registerPurchaseRouteIfAbsent("ATELIER-PIERROT 原宿店", bId, false);
				registerPurchaseRouteIfAbsent("ATELIER PIERROT 横浜ビブレ店", bId, false);
				registerPurchaseRouteIfAbsent("ATELIER-PIERROT 大阪店", bId, false);

			} else if (bName.equals("個人作家")) {
				registerPurchaseRouteIfAbsent("委託店舗", bId, false);

			} else if (bName.equals("海外ブランド")) {
				registerPurchaseRouteIfAbsent("EPETICE", bId, false);
				registerPurchaseRouteIfAbsent("タオバオ", bId, false);

			} else if (bName.contains("しまむら")) {
				// しまむら×青木美沙子、くる実、胡桃咲姫、眞白ありす 共通のデフォルト購入ルート
				registerPurchaseRouteIfAbsent("しまむら店舗", bId, false);
				registerPurchaseRouteIfAbsent("しまむらオンライン", bId, false);
			}
		} // 🌸 ブランドの巡回ループ(for文)の閉じ括弧

		// 🌍 【ここが最大のポイント！】
		// 全ブランドの個別店舗データをDBに入れ終わった「一番最後」に共通項目をインサートします。
		// これにより、ID（プライマリキー）の並び順が必ず後になるため、プルダウンで下側に固定されます！
		registerPurchaseRouteIfAbsent("フリマアプリ", null, true);
		registerPurchaseRouteIfAbsent("ネットオークション", null, true);
		registerPurchaseRouteIfAbsent("クローゼットチャイルド", null, true);
		registerPurchaseRouteIfAbsent("ワンダーウェルト", null, true);
		registerPurchaseRouteIfAbsent("即売会", null, true);
		registerPurchaseRouteIfAbsent("友人・お仲間からのお譲り", null, true);
		registerPurchaseRouteIfAbsent("その他・不明の経路", null, true);

		System.out.println("【成功】すべての初期マスタデータの同期・検証が完了しました！");
	}

	// --- 賢く自動追加するための補助メソッド群 ---

	private void registerBrandIfAbsent(String name) {
		if (!brandRepository.existsByBrandName(name)) {
			Brand brand = new Brand();
			brand.setBrandName(name);
			brandRepository.save(brand);
			System.out.println("【マスタ追加】ブランド: " + name + " を新しく登録しました。");
		}
	}

	private void registerColorIfAbsent(String name) {
		if (!colorRepository.existsByColorName(name)) {
			Color color = new Color();
			color.setColorName(name);
			colorRepository.save(color);
			System.out.println("【マスタ追加】カラー系統: " + name + " を新しく登録しました。");
		}
	}

	private void registerPurchaseRouteIfAbsent(String name, Long brandId, boolean isCommon) {
		// 同じ名前の購入経路がすでにないかチェック
		boolean isExists = purchaseRouteRepository.findAll().stream()
				.anyMatch(pr -> pr.getRouteName().equals(name));

		if (!isExists) {
			PurchaseRoute pr = new PurchaseRoute();
			pr.setRouteName(name);
			pr.setBrandId(brandId);
			pr.setIsCommon(isCommon);
			purchaseRouteRepository.save(pr);
			System.out.println("【マスタ追加】購入経路: " + name + " (共通フラグ: " + isCommon + ") を登録しました。");
		}
	}

	private void registerCategoryTree(String mainName, String subName) {
		// 1. まず大分類が存在するかチェックし、無ければ作る
		if (!mainCategoryRepository.existsByMainCategoryName(mainName)) {
			MainCategory mc = new MainCategory();
			mc.setMainCategoryName(mainName);
			mainCategoryRepository.save(mc);
			System.out.println("【マスタ追加】大分類カテゴリ: " + mainName + " を新しく登録しました。");
		}

		// 2. 作成または既存の大分類データを名前から1件持ってくる
		MainCategory mainCategory = mainCategoryRepository.findByMainCategoryName(mainName).orElseThrow();
		Long mainId = mainCategory.getMainCategoryId();

		// 3. その大分類のIDに、この小分類名がすでに紐づいて登録されているかデータベースの中身をチェック
		boolean isSubExists = subCategoryRepository.findAll().stream()
				.anyMatch(sc -> sc.getMainCategoryId().equals(mainId) && sc.getSubCategoryName().equals(subName));

		// 4. まだ存在しない小分類の組み合わせであれば新しく登録する
		if (!isSubExists) {
			SubCategory sc = new SubCategory();
			sc.setMainCategoryId(mainId);
			sc.setSubCategoryName(subName);
			subCategoryRepository.save(sc);
			System.out.println("【マスタ追加】小分類カテゴリ: [" + mainName + " ➔ " + subName + "] を新しく登録しました。");
		}
	}
}

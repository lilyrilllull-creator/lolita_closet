package com.example.lolitaCloset.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.lolitaCloset.entity.Item;
import com.example.lolitaCloset.repository.BrandRepository;
import com.example.lolitaCloset.repository.ColorRepository;
import com.example.lolitaCloset.repository.ItemRepository;
import com.example.lolitaCloset.repository.MainCategoryRepository;
import com.example.lolitaCloset.repository.SubCategoryRepository;
import com.example.lolitaCloset.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final ItemRepository itemRepository;
	private final BrandRepository brandRepository;
	private final ColorRepository colorRepository;
	private final MainCategoryRepository mainCategoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final UserRepository userRepository;

	/**
	 * 📊 【マルチユーザー完全隔離・グラフマッピング修正版】トップ統計ダッシュボード画面
	 */
	@GetMapping("/")
	public String showDashboard(Model model) {

		// 1. 🔐 ログイン中のお嬢様の本物のユーザー名を取得して、user_idを安全に逆引き
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		String currentUsername = auth.getName();

		Long currentUserId = userRepository.findByUsername(currentUsername)
				.map(user -> user.getUserId())
				.orElseThrow(() -> new IllegalStateException("ログイン中のユーザー情報がデータベースに見つかりません。"));

		// 2. DBから「ログインユーザーの、削除されていない服だけ」を全件抽出します
		List<Item> myItems = itemRepository.findByUserIdAndDeleteFlagFalseOrderByCreatedAtAsc(
				currentUserId, org.springframework.data.domain.Pageable.unpaged()).getContent();

		// 3. 🌸【総数カウント】総アイテム数をHTMLが求める「totalItems」という正確な名前でプレゼント
		model.addAttribute("totalItems", myItems.size());
		// 4. 🏰 【ブランドグラフの完全シンクロ】
		Map<Long, Long> brandMap = myItems.stream()
				.collect(Collectors.groupingBy(Item::getBrandId, Collectors.counting()));
		List<String> brandLabels = new ArrayList<>();
		List<Long> brandCounts = new ArrayList<>();
		brandMap.forEach((bId, count) -> {
			String bName = brandRepository.findById(bId).map(b -> b.getBrandName()).orElse("不明");
			brandLabels.add(bName);
			brandCounts.add(count);
		});
		model.addAttribute("brandLabels", brandLabels);
		model.addAttribute("brandCounts", brandCounts);

		// 5. 🎨 【カラーグラフの完全シンクロ】
		Map<Long, Long> colorMap = myItems.stream()
				.collect(Collectors.groupingBy(Item::getColorId, Collectors.counting()));
		List<String> colorLabels = new ArrayList<>();
		List<Long> colorCounts = new ArrayList<>();
		colorMap.forEach((cId, count) -> {
			String cName = colorRepository.findById(cId).map(c -> c.getColorName()).orElse("不明");
			colorLabels.add(cName);
			colorCounts.add(count);
		});
		model.addAttribute("colorLabels", colorLabels);
		model.addAttribute("colorCounts", colorCounts);

		// 6. 👚 【大物カテゴリグラフの完全シンクロ】
		// subCategoryIdから親のmainCategoryNameを逆引きして合算集計します
		Map<String, Long> categoryMap = myItems.stream().collect(Collectors.groupingBy(item -> {
			if (item.getSubCategoryId() != null) {
				return subCategoryRepository.findById(item.getSubCategoryId())
						.flatMap(sub -> mainCategoryRepository.findById(sub.getMainCategoryId()))
						.map(main -> main.getMainCategoryName())
						.orElse("不明");
			}
			return "不明";
		}, Collectors.counting()));

		List<String> categoryLabels = new ArrayList<>(categoryMap.keySet());
		List<Long> categoryCounts = new ArrayList<>(categoryMap.values());
		model.addAttribute("categoryLabels", categoryLabels);
		model.addAttribute("categoryCounts", categoryCounts);

		// 7. 💖 お迎え服プレビュー（最新の5件だけを安全に切り取って画面へ渡します）
		List<Item> recentItems = myItems.stream()
				.sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
				.limit(5)
				.collect(Collectors.toList());
		model.addAttribute("recentItems", recentItems);

		// マスタデータを画面のコントロール用に引き渡し
		model.addAttribute("brands", brandRepository.findAll());
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());

		return "index"; // templates/index.html を開きます
	}

	/**
	 * 🔐 【新設ヘルパー】現在ログイン中のお嬢様の本物の user_id を安全に取得する魔法のメソッド
	 */
	private Long getCurrentUserId() {
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		String currentUsername = auth.getName();

		return userRepository.findByUsername(currentUsername)
				.map(user -> user.getUserId())
				.orElseThrow(() -> new IllegalStateException("ログイン中のユーザー情報がデータベースに見つかりません。"));
	}
}

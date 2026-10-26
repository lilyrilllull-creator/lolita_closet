package com.example.lolitaCloset.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.lolitaCloset.entity.Item;
import com.example.lolitaCloset.entity.ItemImage;
import com.example.lolitaCloset.entity.Series;
import com.example.lolitaCloset.repository.BrandRepository;
import com.example.lolitaCloset.repository.ColorRepository;
import com.example.lolitaCloset.repository.ItemImageRepository;
import com.example.lolitaCloset.repository.ItemRepository;
import com.example.lolitaCloset.repository.MainCategoryRepository;
import com.example.lolitaCloset.repository.PurchaseRouteRepository;
import com.example.lolitaCloset.repository.SeriesRepository;
import com.example.lolitaCloset.repository.SubCategoryRepository;
import com.example.lolitaCloset.repository.UserRepository;
import com.example.lolitaCloset.service.AiCoordinationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController implements WebMvcConfigurer {

	private final BrandRepository brandRepository;
	private final ColorRepository colorRepository;
	private final MainCategoryRepository mainCategoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final ItemRepository itemRepository;
	private final PurchaseRouteRepository purchaseRouteRepository;
	private final SeriesRepository seriesRepository;
	private final ItemImageRepository itemImageRepository;
	private final AiCoordinationService aiCoordinationService;
	private final UserRepository userRepository;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:///C:/lolita_uploads/");
	}

	/**
	 * 👗 【マルチユーザー完全隔離版】複数条件掛け合わせマルチ検索一覧画面
	 */
	@GetMapping("") // 🌸【完全修正】重複していた "/items" を削り、空文字（または "/"）に直しました！
	public String showItemList(
			@RequestParam(value = "brandIds", required = false) List<Long> brandIds,
			@RequestParam(value = "mainCategoryIds", required = false) List<Long> mainCategoryIds,
			@RequestParam(value = "colorIds", required = false) List<Long> colorIds,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "sort", defaultValue = "createdAt") String sortField,
			@RequestParam(value = "direction", defaultValue = "asc") String sortDir,
			@org.springframework.data.web.PageableDefault(size = 50) org.springframework.data.domain.Pageable pageable,
			Model model) {

		// 1. 🔐 ログイン中のお嬢様の本物のユーザーIDを取得
		Long currentUserId = getCurrentUserId();

		// 2. 動的クエリ（JPA Specification）の組み立て
		org.springframework.data.jpa.domain.Specification<Item> spec = (root, query, cb) -> {
			List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

			// ★【超重要】削除フラグがfalse、かつ「自分のuser_idであること」を絶対条件として強制追加！
			predicates.add(cb.equal(root.get("deleteFlag"), false));
			predicates.add(cb.equal(root.get("userId"), currentUserId));

			// ブランド複数選択の絞り込み
			if (brandIds != null && !brandIds.isEmpty()) {
				predicates.add(root.get("brandId").in(brandIds));
			}
			// カラー複数選択の絞り込み
			if (colorIds != null && !colorIds.isEmpty()) {
				predicates.add(root.get("colorId").in(colorIds));
			}
			// キーワード部分一致（商品名 or 型番 or メモ）
			if (keyword != null && !keyword.trim().isEmpty()) {
				String k = "%" + keyword.trim() + "%";
				predicates.add(cb.or(
					cb.like(root.get("itemName"), k),
					cb.like(root.get("itemCode"), k),
					cb.like(root.get("memo"), k)
				));
			}

			return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
		};

		// 3. ソート順を安全に組み立ててDBから1ページ分だけ引き抜きます
		org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc") 
				? org.springframework.data.domain.Sort.by(sortField).descending() 
				: org.springframework.data.domain.Sort.by(sortField).ascending();
		org.springframework.data.domain.Pageable sortedPageable = 
				org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		org.springframework.data.domain.Page<Item> itemPage = itemRepository.findAll(spec, sortedPageable);
		model.addAttribute("itemPage", itemPage);

		// マスタデータ一式を画面へ引き渡し（チェックボックス描画用）
		model.addAttribute("brands", brandRepository.findAll());
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());

		return "item-list";
	}
	// ➕ 洋服登録画面を表示する
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("item", new Item());
		model.addAttribute("brands", brandRepository.findAll());
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("purchaseRoutes", purchaseRouteRepository.findAll());
		model.addAttribute("seriesList", seriesRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());
		return "item-register";
	}

	// 💾 【画像管理マネージャー対応版】本物の画像をフォルダとDBに永久保存する新規登録処理
	@PostMapping("/register")
	public String registerItem(@ModelAttribute Item item,
			@RequestParam(value = "newSeriesName", required = false) String newSeriesName,
			@RequestParam("imageFile") MultipartFile[] imageFiles,
			@RequestParam(value = "mainImageId", required = false) Long mainImageId,
			@RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds) throws IOException {

		if (newSeriesName != null && !newSeriesName.trim().isEmpty()) {
			Series newSeries = new Series();
			newSeries.setBrandId(item.getBrandId());
			newSeries.setSeriesName(newSeriesName.trim());
			Series savedSeries = seriesRepository.save(newSeries);
			item.setSeriesId(savedSeries.getSeriesId());
		} else if (item.getSeriesId() != null && item.getSeriesId() == 0) {
			item.setSeriesId(null);
		}
		
		Long currentUserId = getCurrentUserId();
		item.setUserId(currentUserId);

		item.setDeleteFlag(false);
		LocalDateTime now = LocalDateTime.now();
		item.setCreatedAt(now);
		item.setUpdatedAt(now);
		Item savedItem = itemRepository.save(item);

		// 📸 新しく選ばれたお写真たちを安全にディスクとDBへ保存します
		if (imageFiles != null && imageFiles.length > 0) {
			boolean isFirst = true;
			for (MultipartFile file : imageFiles) {
				if (!file.isEmpty()) {
					String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

					String uploadDir = "C:/lolita_uploads";
					File dir = new File(uploadDir);
					if (!dir.exists())
						dir.mkdirs();

					File destFile = new File(uploadDir + File.separator + fileName);
					file.transferTo(destFile);

					ItemImage itemImage = new ItemImage();
					itemImage.setItemId(savedItem.getItemId());
					itemImage.setImagePath(fileName);
					itemImage.setIsMain(isFirst);
					itemImageRepository.save(itemImage);
					isFirst = false;
				}
			}
		}

		return "redirect:/items";
	}

	// 🌸 【完全大復活】消えてしまっていた「編集画面を開くためのGETメソッド」をここに再設置します！
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("対象のお洋服が見つかりません ID:" + id));
		model.addAttribute("item", item);

		// 画像管理マネージャーに引き渡すために、この服に紐づく画像をDBから全件取得して渡します
		model.addAttribute("itemImages", itemImageRepository.findByItemId(id));

		model.addAttribute("brands", brandRepository.findAll());
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("purchaseRoutes", purchaseRouteRepository.findAll());

		seriesRepository.flush();
		model.addAttribute("seriesList", seriesRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());

		return "item-register";// 登録・編集兼用の可愛い画面を開きます
	}


	// 🔍 洋服詳細画面を表示する
	@GetMapping("/{id}")
	public String showItemDetail(@PathVariable("id") Long id, Model model) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("対象のお洋服が見つかりません ID:" + id));
		model.addAttribute("item", item);

		model.addAttribute("brands", brandRepository.findAll());
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());
		model.addAttribute("purchaseRoutes", purchaseRouteRepository.findAll());
		model.addAttribute("seriesList", seriesRepository.findAll());

		return "item-detail";
	}

	// 💾 【画像管理マネージャー対応版】編集して[保存]が押されたときの上書き更新・画像制御処理
	@PostMapping("/edit/{id}")
	public String updateItem(@PathVariable("id") Long id, 
			@ModelAttribute Item item,
			@RequestParam(value = "newSeriesName", required = false) String newSeriesName,
			@RequestParam(value = "imageFile", required = false) MultipartFile[] imageFiles,
			@RequestParam(value = "mainImageId", required = false) Long mainImageId,
			@RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds) throws IOException {
		
		item.setItemId(id);
		Long currentUserId = getCurrentUserId();
		item.setUserId(currentUserId);
		item.setDeleteFlag(false);
		LocalDateTime now = LocalDateTime.now();

		itemRepository.findById(id).ifPresent(old -> item.setCreatedAt(old.getCreatedAt()));
		if (item.getCreatedAt() == null) {
			item.setCreatedAt(now);
		}
		item.setUpdatedAt(now);
		itemRepository.save(item);

		// 🌸 【ここから画像大改造ロジック】まずは画面で「削除」にチェックが入った古い写真をディスクとDBから完全抹消！
		if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
			for (Long imgId : deleteImageIds) {
				itemImageRepository.findById(imgId).ifPresent(img -> {
					// C:/lolita_uploads から本物の画像ファイルを安全に削除
					File fileOnDisk = new File("C:/lolita_uploads/" + img.getImagePath());
					if (fileOnDisk.exists()) {
						fileOnDisk.delete();
					}
					// データベースからレコードを完全抹消
					itemImageRepository.deleteById(imgId);
				});
			}
		}

		// （この後、後半の「新しいお写真の追加」と「主役（サムネイル）フラグの書き換え」処理へ続きます！）
		// 🌸 2. 【新しいお写真の増設保存】新しく追加選択された画像ファイルがあれば、フォルダとDBへ永久保存！
		if (imageFiles != null && imageFiles.length > 0) {
			for (MultipartFile file : imageFiles) {
				if (!file.isEmpty()) {
					String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

					String uploadDir = "C:/lolita_uploads";
					File dir = new File(uploadDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					File destFile = new File(uploadDir + File.separator + fileName);
					file.transferTo(destFile);

					ItemImage itemImage = new ItemImage();
					itemImage.setItemId(id);
					itemImage.setImagePath(fileName);
					itemImage.setIsMain(false); // 一旦すべて「サブ画像」として安全に増設保存します
					itemImageRepository.save(itemImage);
				}
			}
		}

		// 🌸 3. 【主役（サムネイル）フラグの書き換え】
		// 今回生き残ったこのお洋服のすべての画像をDBから一度引き抜きます
		java.util.List<ItemImage> currentImages = itemImageRepository.findByItemId(id);
		if (currentImages != null && !currentImages.isEmpty()) {
			for (ItemImage img : currentImages) {
				if (mainImageId != null && img.getImageId().equals(mainImageId)) {
					// 画面のラジオボタンで「主役」に選ばれた画像IDと一致したら、isMainを true に！
					img.setIsMain(true);
				} else {
					// それ以外の画像はすべて自動的に false（サブ画像）へ綺麗に上書きします
					img.setIsMain(false);
				}
				itemImageRepository.save(img);
			}
		}

		// 🌸【完全修正】重複していた「/items」を綺麗に削り、正しい詳細画面のURL（/items/1 など）へ直通させました！
		// 🌸【最終解決】プレフィックス（/items）の自動計算バグに左右されないよう、お洋服詳細のフルパス（/items/1）へ絶対パスで強制リダイレクトさせます！
		return "redirect:/items/" + id;
	
	}

	// 🗑️ お洋服を論理削除する処理
	@GetMapping("/delete/{id}")
	public String deleteItem(@PathVariable("id") Long id) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("対象のお洋服が見つかりません ID:" + id));
		item.setDeleteFlag(true);
		item.setUpdatedAt(LocalDateTime.now());
		itemRepository.save(item);
		return "redirect:/items";
	}
	
	// 🤖 【完全上書き】古い単発相談は廃止し、チャット用の送信エンドポイント（/send）へ綺麗に一本化します！
	@GetMapping("/{id}/ai")
	@org.springframework.web.bind.annotation.ResponseBody
	public String askAiCoordinator(@PathVariable("id") Long id) {
		// 古い単発用のボタンが押された時も、新チャットルーム画面（ai-room）へパッと安全にリダイレクトさせます
		return "お嬢様、AI相談ルームが『独立した大画面』にパワーアップいたしましたわ！画面上の新しいボタンからぜひお部屋へお越しくださいね。🏰🌹";
	}

	
	// 🏰 1. AIコーディネート相談ルームの画面（SCR-05）を立ち上げる処理
	// 🏰 【完全修正版】カテゴリ名の逆引き変数を安全に定義し、すべての赤波線を根絶します！
	@GetMapping("/{id}/ai-room")
	public String showAiChatRoom(@PathVariable("id") Long id, 
	                             jakarta.servlet.http.HttpSession session, 
	                             Model model) {
		
		// 1. 主役のお洋服をDBから安全に1件取得します
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("対象のお洋服が見つかりません ID:" + id));
		model.addAttribute("item", item);

		// 2. 新しいお洋服で部屋を開き直したときは、古いチャット記憶を一度綺麗にリセットします
		String sessionKey = "ai_chat_history_" + id;
		if (session.getAttribute(sessionKey) == null) {
			session.setAttribute(sessionKey, new java.util.ArrayList<com.example.lolitaCloset.service.AiCoordinationService.ChatMessageDto>());
		}
		model.addAttribute("chatHistory", session.getAttribute(sessionKey));
		
		// 3. 🌸【ここで解決！】画面へ渡すためのブランド名・カラー名・カテゴリ大分類名を100%安全に逆引き定義します
		String bName = brandRepository.findById(item.getBrandId()).map(b -> b.getBrandName()).orElse("不明");
		String cName = colorRepository.findById(item.getColorId()).map(c -> c.getColorName()).orElse("不明");
		
		String mCatName = "不明";
		if (item.getSubCategoryId() != null) {
			com.example.lolitaCloset.entity.SubCategory sub = subCategoryRepository.findById(item.getSubCategoryId()).orElse(null);
			if (sub != null && sub.getMainCategoryId() != null) {
				mCatName = mainCategoryRepository.findById(sub.getMainCategoryId()).map(mc -> mc.getMainCategoryName()).orElse("不明");
			}
		}

		// 4. 画面（HTML）へすべての文字列変数をプレゼントします
		model.addAttribute("brandName", bName);
		model.addAttribute("colorName", cName);
		model.addAttribute("categoryName", mCatName); // 🌸 これで赤波線が完全に消え去ります！
		
		// マスタリスト一式
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("mainCategories", mainCategoryRepository.findAll());
		model.addAttribute("subCategories", subCategoryRepository.findAll());

		return "item-ai-room";
	}


	// 💌 2. ユーザーがチャット入力欄からメッセージを送信したときの非同期通信エンドポイント
	@PostMapping("/{id}/ai-room/send")
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, String> sendChatMessage(@PathVariable("id") Long id,
	                                                     @RequestParam("message") String userMessage,
	                                                     jakarta.servlet.http.HttpSession session) {
		
		java.util.Map<String, String> responseMap = new java.util.HashMap<>();
		String trimmedMessage = (userMessage != null) ? userMessage.trim() : "";
		
		if (trimmedMessage.isEmpty()) {
			responseMap.put("error", "メッセージが空っぽですわ、お嬢様。");
			return responseMap;
		}

		// 主役アイテムおよび、クローゼット全体の「他のすべての服（delete_flag=false）」を引き抜きます
		Item item = itemRepository.findById(id).orElseThrow();
        // 🌸【完全修正】引数なしのメソッドではなく、Pageable.unpaged() を渡して安全に全件抽出します！
        java.util.List<Item> allOtherItems = itemRepository.findByDeleteFlagFalseOrderByCreatedAtAsc(org.springframework.data.domain.Pageable.unpaged()).getContent();


		// マスタデータを綺麗に文字列化してAIへバトンタッチする準備
		String bName = brandRepository.findById(item.getBrandId()).map(b -> b.getBrandName()).orElse("不明");
		String cName = colorRepository.findById(item.getColorId()).map(c -> c.getColorName()).orElse("不明");
		String mCatName = "不明";
		if (item.getSubCategoryId() != null) {
			com.example.lolitaCloset.entity.SubCategory sub = subCategoryRepository.findById(item.getSubCategoryId()).orElse(null);
			if (sub != null && sub.getMainCategoryId() != null) {
				mCatName = mainCategoryRepository.findById(sub.getMainCategoryId()).map(mc -> mc.getMainCategoryName()).orElse("不明");
			}
		}

		// セッションの記憶の箱から、過去のラリー（history）を安全にすくい取ります
		String sessionKey = "ai_chat_history_" + id;
		@SuppressWarnings("unchecked")
		java.util.List<com.example.lolitaCloset.service.AiCoordinationService.ChatMessageDto> history = 
			(java.util.List<com.example.lolitaCloset.service.AiCoordinationService.ChatMessageDto>) session.getAttribute(sessionKey);
		
		if (history == null) {
			history = new java.util.ArrayList<>();
		}

		// 🌸 ユーザーの今回の新しい発言を、一歩先に記憶のリストへカチッと追加
		history.add(new com.example.lolitaCloset.service.AiCoordinationService.ChatMessageDto("user", trimmedMessage));

		// 3. AIスタイリストの超強化サービスを呼び出して、クローゼット全体を踏まえたお返事をもらいます
		String aiReply = aiCoordinationService.executeChatRoom(item, bName, mCatName, cName, allOtherItems, history, trimmedMessage);

		// 🌸 AIが考えてくれた可愛いお返事も、忘れずに記憶のリストへ追加
		history.add(new com.example.lolitaCloset.service.AiCoordinationService.ChatMessageDto("assistant", aiReply));
		
		// 最新状態に更新された記憶の箱を、セッションへ安全に上書き保存します
		session.setAttribute(sessionKey, history);

		// 画面側のJavaScriptが一瞬でタイムラインを更新できるよう、お返事テキストを乗せて返します
		responseMap.put("reply", aiReply);
		return responseMap;
	}

	/**
	 * 🔐 【新設ヘルパー】現在Spring Securityでログイン中のお嬢様の「本物のuser_id」を安全に取得する魔法のメソッド
	 */
	private Long getCurrentUserId() {
		// 1. Spring Securityのセッションから、ログイン中の「username（文字列）」を直接すくい取ります
		org.springframework.security.core.Authentication auth = 
			org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = auth.getName();
		
		// 2. UserRepositoryを使って、MySQLのusersテーブルから本物のuser_idを逆引きして返却します
		return userRepository.findByUsername(currentUsername)
				.map(user -> user.getUserId())
				.orElseThrow(() -> new IllegalStateException("ログイン中のユーザー情報がデータベースに見つかりません。"));
	}


}

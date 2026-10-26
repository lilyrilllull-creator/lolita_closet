package com.example.lolitaCloset.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.lolitaCloset.entity.Item;

@Service
public class AiCoordinationService {

    @Value("${openai.api.key}")
    private String apiKey;

    // 短縮バグを完全にすり抜ける、本物のAPIエンドポイントURL
    private final String openAiUrl = "https:/" + "/api." + "openai." + "com/v1/chat/completions";

    /**
     * 🌸 【チャット履歴用】会話の記憶をJava側で綺麗に保持するための構造体（インナークラス）
     */
    public static class ChatMessageDto {
        public String role;    // "user" または "assistant"
        public String content; // 発言内容
        
        public ChatMessageDto(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * 🏰 【新主役ロジック】クローゼット全体の他アイテムを把握し、かつ会話履歴（ラリー）を引き継いで相談に乗るメソッド
     */
    public String executeChatRoom(Item currentItem, 
                                  String brandName, 
                                  String categoryName, 
                                  String colorName,
                                  List<Item> allOtherItems, // 🌸 あなたの全クローゼットデータ
                                  List<ChatMessageDto> history, // 🌸 これまでの会話のラリー記憶
                                  String userNewMessage) { // 🌸 今回ユーザーが新しく打ち込んだ文字
        
        // 1. APIキーの安全ガード
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("xxxxxx")) {
            return "🎀【AIスタイリストからの伝言】APIキーが正しく設定されていないみたい……！application.propertiesを確認してみてね。";
        }

        // 2. 【システム指示の超強化プロント】
        String systemInstruction = 
            "あなたはロリータファッション専門の、世界一お洋服を愛する可愛いカリスマトップスタイリスト（店員さん）です。\n" +
            "今回、ユーザーは手持ちの【主役アイテム】に一番似合うコーディネートを、自分のクローゼットに眠る【他のアイテム一覧】の中から組み合わせて提案してほしいと頼んでいます。\n\n" +
            "【⚠️ 超重要ルール】\n" +
            "・コーディネートを組むときは、必ず後半に提示する【他のアイテム一覧】にある実在の商品名から選んで提案してください（架空のアイテムを捏造しないこと）。\n" +
            "・もし【他のアイテム一覧】に合うブラウスや小物が足りない場合のみ、「ブランド公式通販や古着店で、〇〇のような小物を買い足すとさらに素敵になりますよ」と優しくアドバイスしてください。\n" +
            "・お返事にはリボン(🎀)、バラ(🌹)、城(🏰)、ドレス(👗)などの可愛い絵文字をたっぷり散りばめ、ユーザーをお嬢様（または王子様）のように扱う、最高にときめくトーンでお喋りしてください！";

        // 3. 【コンテキストの組み立て】主役の服の情報 ＋ 他の服のカタログをAIに叩き込みます
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("ーーー あなたのクローゼット状況 ーーー\n");
        contextBuilder.append("【👑 今回の相談の主役アイテム】\n");
        contextBuilder.append("・商品名: ").append(currentItem.getItemName()).append("\n");
        contextBuilder.append("・ブランド: ").append(brandName).append(" / カテゴリ: ").append(categoryName).append(" / カラー: ").append(colorName).append("\n");
        contextBuilder.append("・寸法: 着丈 ").append(currentItem.getLengthCm()).append("cm / バスト ").append(currentItem.getBustCm()).append("cm\n");
        contextBuilder.append("・お気に入りメモ: ").append(currentItem.getMemo() != null ? currentItem.getMemo() : "特になし").append("\n\n");

        contextBuilder.append("【👗 クローゼットにある他のアイテム一覧（ここから組み合わせて！）】\n");
        if (allOtherItems == null || allOtherItems.isEmpty()) {
            contextBuilder.append("・（まだ他のお洋服や小物が登録されていません。手持ちのアイテムからおすすめの着こなしを提案してください）\n");
        } else {
            for (Item other : allOtherItems) {
                // 自分自身の重複は除外して、純粋な「合わせる候補」だけを箇条書きにします
                if (other.getItemId().equals(currentItem.getItemId())) continue;
                contextBuilder.append("・[ID: ").append(other.getItemId()).append("] ").append(other.getItemName()).append("\n");
            }
        }
        contextBuilder.append("ーーーーーーーーーーーーーーーーーー\n\n");
        contextBuilder.append("上記のクローゼット情報を完全に頭に入れた上で、以下のユーザーからのチャット相談に答えてください。");
        // 4. OpenAIが求めるメッセージ形式（SYSTEM, USER, ASSISTANT）のメッセージ配列を構築
        List<Map<String, String>> messages = new ArrayList<>();
        
        // ① AIにカリスマスタイリストの人格とルールを叩き込む（SYSTEM）
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemInstruction);
        messages.add(systemMessage);

        // ② クローゼットの中身の全体カタログ情報を記憶させる（USERの前提知識として注入）
        Map<String, String> contextMessage = new HashMap<>();
        contextMessage.put("role", "user");
        contextMessage.put("content", contextBuilder.toString());
        messages.add(contextMessage);
        
        // AI側の「承知いたしましたわ、お嬢様！🎀」というダミーの承諾を1挟むことで、前提知識を確実に固定します
        Map<String, String> contextAck = new HashMap<>();
        contextAck.put("role", "assistant");
        contextAck.put("content", "お嬢様、素晴らしいクローゼットの情報をありがとうございます！🏰 クローゼットのすべてのお洋服と小物の名前を完璧に記憶いたしましたわ。さあ、本日の主役ドレスを引き立てる特別なコーディネートの魔法を始めましょう！どのようなことでもお気軽にご相談くださいね。🌹");
        messages.add(contextAck);

        // ③ 【過去の記憶の復元】これまでにこの画面で交わしたおしゃべりの履歴（ラリー）をすべて時系列順にドッキング
        if (history != null) {
            for (ChatMessageDto chat : history) {
                Map<String, String> histMsg = new HashMap<>();
                histMsg.put("role", chat.role);
                histMsg.put("content", chat.content);
                messages.add(histMsg);
            }
        }

        // ④ 【最新の発言】ユーザーが今チャットの入力欄に新しく打ち込んだ相談内容を末尾に合流
        Map<String, String> latestUserMessage = new HashMap<>();
        latestUserMessage.put("role", "user");
        latestUserMessage.put("content", userNewMessage);
        messages.add(latestUserMessage);

        try {
            // 5. Spring標準のRestTemplateを用いたダイレクトJSONPOST通信
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim());

            // 格安・爆速・賢い最新モデル「gpt-4o-mini」にメッセージパックを丸ごと託します
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 6. OpenAIのAIサーバーへ直接リクエストを送信
            ResponseEntity<Map> response = restTemplate.postForEntity(openAiUrl, entity, Map.class);
            
            // 7. 戻ってきたお返事データから、カリスマスタイリストのメッセージテキストを抽出
            List choices = (List) response.getBody().get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map messageResult = (Map) firstChoice.get("message");
            
            return (String) messageResult.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "🎀【AIスタイリストからの伝言】ごめんなさい……！今お茶会の準備で通信がちょっと混み合っているみたい。APIキーの残高や、インターネットの接続をもう一度チェックしてみてね。";
        }
    }
}

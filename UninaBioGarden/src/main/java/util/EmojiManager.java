package util;

import java.util.HashMap;
import java.util.Map;

public class EmojiManager {

    private static EmojiManager instance;
    private Map<String, String> emojiPerColtura;

    private EmojiManager() {
        inizializzaEmoji();
    }

    public static EmojiManager getInstance() {
        if (instance == null) {
            instance = new EmojiManager();
        }
        return instance;
    }

    private void inizializzaEmoji() {
        emojiPerColtura = new HashMap<>();
        emojiPerColtura.put("Carota", "🥕");
        emojiPerColtura.put("Pomodoro", "🍅");
        emojiPerColtura.put("Mais", "🌽");
        emojiPerColtura.put("Lattuga", "🥬");
        emojiPerColtura.put("Melanzana", "🍆");
        emojiPerColtura.put("Peperone", "🫑");
        emojiPerColtura.put("Cavolo", "🥦");
        emojiPerColtura.put("Patata", "🥔");
        emojiPerColtura.put("Zucca", "🎃");
        emojiPerColtura.put("Fragola", "🍓");
    }

    public String getEmoji(String nomeColtura) {
        return emojiPerColtura.getOrDefault(nomeColtura, "❓");
    }
}
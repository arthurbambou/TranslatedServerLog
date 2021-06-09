package fr.catcore.server.translations.api.resource.language;

import fr.catcore.server.translations.api.ServerTranslations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;

import java.util.Optional;

public final class SystemDelegatedLanguage extends Language {
    public static final SystemDelegatedLanguage INSTANCE = new SystemDelegatedLanguage();

    private Language vanilla = Language.getInstance();

    private SystemDelegatedLanguage() {
    }

    public void setVanilla(Language language) {
        this.vanilla = language;
    }

    @Override
    public String get(String key) {
        String override = this.getSystemLanguage().local().getOrNull(key);
        if (override != null) {
            return override;
        }
        return this.vanilla.get(key);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.vanilla.hasTranslation(key) || this.getSystemLanguage().local().contains(key);
    }

    @Override
    public boolean isRightToLeft() {
        return this.getSystemLanguage().definition().rightToLeft();
    }

    private ServerLanguage getSystemLanguage() {
        return ServerTranslations.INSTANCE.getSystemLanguage();
    }

    @Override
    public OrderedText reorder(StringVisitable text) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return ReorderingUtil.reorder(text, this.isRightToLeft());
        } else {
            return (visitor) -> {
                return text.visit((style, string) -> {
                    return TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT;
                }, Style.EMPTY).isPresent();
            };
        }
    }
}

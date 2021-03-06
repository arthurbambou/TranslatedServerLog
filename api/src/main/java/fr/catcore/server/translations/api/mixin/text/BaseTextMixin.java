package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseText.class)
public abstract class BaseTextMixin implements LocalizableText {
    @Override
    public void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        Style selfStyle = this.getStyle().withParent(style);
        this.visitSelfLocalized(visitor, target, selfStyle);

        for (Text sibling : this.getSiblings()) {
            if (sibling instanceof LocalizableText) {
                ((LocalizableText) sibling).visitLocalized(visitor, target, selfStyle);
            } else {
                sibling.visit(visitor.asGeneric(selfStyle));
            }
        }
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        visitor.acceptLiteral(this.asString(), style);
    }
}

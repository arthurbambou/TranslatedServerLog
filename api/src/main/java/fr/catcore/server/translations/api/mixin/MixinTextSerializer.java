package fr.catcore.server.translations.api.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import fr.catcore.server.translations.api.LocalizedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
public abstract class MixinTextSerializer {
    @Shadow
    public abstract JsonElement serialize(Text text, Type type, JsonSerializationContext ctx);

    @Inject(
            method = "serialize",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void serializeTranslatableText(Text text, Type type, JsonSerializationContext ctx, CallbackInfoReturnable<JsonElement> ci) {
        if (text instanceof LocalizedText) {
            Text literal = ((LocalizedText) text).asLiteral();
            ci.setReturnValue(this.serialize(literal, literal.getClass(), ctx));
        }
    }
}

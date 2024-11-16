package dev.xdpxi.xdlib.mixin.client;

import com.google.common.collect.Lists;
import dev.xdpxi.xdlib.XDsLibrary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {
    @Unique
    private final List<ButtonWidget> buttons = Lists.newArrayList();

    @Unique
    public boolean showRespawnButton = false;

    @Unique
    public int ticksSinceShiftPress;

    @Unique
    public int ticksSinceDeath;

    @Unique
    public int buttonActivationTicks;

    @Unique
    public int ticksUntilRespawn;

    @Unique
    public int secondsUntilRespawn;

    @Unique
    public int respawnDelayTicks = 5 * 20;

    @Unique
    public int ticksToSeconds;

    @Unique
    public boolean wasHudHidden;

    @Unique
    public boolean hudWasHiddenByMod;

    @Unique
    public boolean hasHudBeenDeterminedOnce = false;

    @Unique
    String respawnMessage = "Respawning in <time>";

    @Unique
    SoundInstance soundInstance = PositionedSoundInstance.master(XDsLibrary.DEATH_SOUND_EVENT, 1.0f, 1.0f);

    @Unique
    private Text deathReasonMessage;

    @Unique
    private boolean hardcore;

    @Unique
    private Text respawnText = Text.of("0");

    @Unique
    private Text deathCoords;

    @Unique
    private String deathCoordsMessage;

    public DeathScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Text message, boolean isHardcore, CallbackInfo ci) {
        this.deathReasonMessage = message;
        this.hardcore = isHardcore;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        ci.cancel();
        if (!hasHudBeenDeterminedOnce) {
            wasHudHidden = this.client.options.hudHidden;
            hasHudBeenDeterminedOnce = true;
        }
        buttonActivationTicks = 0;

        this.buttons.clear();
        this.buttons.removeAll(buttons);

        this.deathCoords = Text.of(this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ());
        this.deathCoordsMessage = "Death coordinates: " + this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ();
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        ticksSinceDeath = 0;
        ticksUntilRespawn = respawnDelayTicks;
        secondsUntilRespawn = respawnDelayTicks / 20;
        ticksToSeconds = 0;
        ticksSinceShiftPress = 0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Unique
    private void setButtonsActive(boolean active) {
        ButtonWidget buttonWidget;
        for (Iterator var2 = this.buttons.iterator(); var2.hasNext(); buttonWidget.active = active) {
            buttonWidget = (ButtonWidget) var2.next();
        }

    }

    @Unique
    private void titleScreenWasClicked() {
        if (this.hardcore) {
            this.quitLevel();
        } else {
            ConfirmScreen confirmScreen = new DeathScreen.TitleScreenConfirmScreen((confirmed) -> {
                if (confirmed) {
                    this.quitLevel();
                } else {
                    this.client.player.requestRespawn();
                    respawnReset();
                    this.client.setScreen(null);
                }

            }, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
            this.client.setScreen(confirmScreen);
            confirmScreen.disableButtons(20);
        }
    }

    @Unique
    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect();
        }
        this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        this.client.setScreen(new TitleScreen());
    }

    @Unique
    public void respawnReset() {
        if (!wasHudHidden && hudWasHiddenByMod) {
            this.client.options.hudHidden = false;
        }

        this.client.getSoundManager().stop(soundInstance);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        int defaultFadeDelay = 7;
        int fade = 50;

        context.fillGradient(0, 0, this.width, this.height, 0x60500000, -1602211792);
        context.getMatrices().push();
        context.getMatrices().scale(2.0F, 2.0F, 2.0F);

        int fadeDelayDeathMessage = defaultFadeDelay;
        if (ticksSinceDeath >= fadeDelayDeathMessage) {
            float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
            int color = new Color(240, 159, 161).getRGB();
            int fadeColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            context.drawCenteredTextWithShadow(this.textRenderer, "You were slain...", this.width / 2 / 2, 30, fadeColor);
        }

        int fadeDelayRespawnMessage = defaultFadeDelay;
        if (ticksSinceDeath >= fadeDelayRespawnMessage) {
            float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
            int color = new Color(240, 130, 132).getRGB();
            int fadeColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            context.drawCenteredTextWithShadow(this.textRenderer, this.respawnText, this.width / 2 / 2, 68, fadeColor);
        }
        context.getMatrices().pop();

        if (ticksSinceDeath >= fadeDelayRespawnMessage && showRespawnButton) {
            float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
            int color = new Color(240, 130, 132).getRGB();
            int fadeColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            context.drawCenteredTextWithShadow(this.textRenderer, this.respawnText, this.width / 2, 123, fadeColor);
        }

        int fadeDelayDeathReason = defaultFadeDelay;
        if (ticksSinceDeath >= fadeDelayDeathReason && this.deathReasonMessage != null) {
            float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
            int color = new Color(240, 159, 161).getRGB();
            int fadeColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            context.drawCenteredTextWithShadow(this.textRenderer, this.deathReasonMessage, this.width / 2, 85, fadeColor);
        }
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.deathReasonMessage == null) {
            return null;
        }
        int i = this.client.textRenderer.getWidth(this.deathReasonMessage);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        if (mouseX < j || mouseX > k) {
            return null;
        }
        return this.client.textRenderer.getTextHandler().getStyleAt(this.deathReasonMessage, mouseX - j);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        ci.cancel();
        ++this.ticksSinceDeath;
        ++this.buttonActivationTicks;
        ++this.ticksToSeconds;
        --this.ticksUntilRespawn;

        this.respawnText = Text.of(respawnMessage.replaceAll("<time>", String.valueOf(secondsUntilRespawn)));

        if (ticksSinceDeath == 3) {
            this.client.getSoundManager().play(soundInstance);
        }

        if (ticksToSeconds == 20) {
            --secondsUntilRespawn;
            ticksToSeconds = 0;
        }

        if (buttonActivationTicks == 20) {
            this.setButtonsActive(true);
        }
        if (this.ticksUntilRespawn == 0) {
            this.client.player.requestRespawn();
            respawnReset();
        }
    }
}
package com.seabreyh.mana.foundation.client.gui.screens;

import com.seabreyh.mana.foundation.event.player.PlayerWishEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WishViewScreen extends Screen {
    public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
    public static final int PAGE_COUNT = 3;
    public static final int PAGE_TEXT_X_OFFSET = 36;
    public static final int PAGE_TEXT_Y_OFFSET = 30;

    public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
    private final List<String> MESSAGES = Arrays.asList("I wish for sunshine", "I wish for storms",
            "I wish I had a friend");

    protected static final int TEXT_WIDTH = 114;
    protected static final int TEXT_HEIGHT = 128;
    protected static final int IMAGE_WIDTH = 192;
    protected static final int IMAGE_HEIGHT = 192;
    private int currentPage;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
    private int cachedPage = -1;
    private Component pageMsg = Component.empty();
    private Button submitButton;
    private PageButton forwardButton;
    private PageButton backButton;
    private final boolean playTurnSound;

    public WishViewScreen() {
        super(Component.empty());
        // super(NarratorChatListener.NO_TITLE);

        this.playTurnSound = true;
    }

    public boolean setPage(int p_98276_) {
        int i = Mth.clamp(p_98276_, 0, WishViewScreen.PAGE_COUNT - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
            this.cachedPage = -1;
            return true;
        } else {
            return false;
        }
    }

    protected boolean forcePage(int p_98295_) {
        return this.setPage(p_98295_);
    }

    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    protected void createMenuControls() {

        submitButton = new PlainTextButton(width / 2 - 50, 196, 200, 20,
                Component.translatable("gui.mana.select_wish"), (p_280834_) -> {
                    this.minecraft.setScreen((Screen) null);
                    PlayerWishEvent.makeWishFromIndx(this.currentPage);
                }, this.font);

        addRenderableWidget(submitButton);
    }

    protected void createPageControlButtons() {
        int i = (this.width - 192) / 2;
        // int j = 2;
        this.forwardButton = this.addRenderableWidget(new PageButton(i + 116, 159,
                true, (p_98297_) -> {
                    this.pageForward();
                }, this.playTurnSound));
        this.backButton = this.addRenderableWidget(new PageButton(i + 43, 159, false,
                (p_98287_) -> {
                    this.pageBack();
                }, this.playTurnSound));
        this.updateButtonVisibility();
    }

    private int getNumPages() {
        return WishViewScreen.PAGE_COUNT;
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }

        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        }

        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = true;
        this.backButton.visible = true;
    }

    public boolean keyPressed(int p_98278_, int p_98279_, int p_98280_) {
        if (super.keyPressed(p_98278_, p_98279_, p_98280_)) {
            return true;
        } else {
            switch (p_98278_) {
                case 266:
                    this.backButton.onPress();
                    return true;
                case 267:
                    this.forwardButton.onPress();
                    return true;
                default:
                    return false;
            }
        }
    }

    public void render(GuiGraphics graphics, int p_98283_, int p_98284_, float p_98285_) {
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BOOK_LOCATION);
        int i = (this.width - 192) / 2;
        // int j = 2;
        graphics.blit(BOOK_LOCATION, i, 2, 0, 0, 192, 192);
        // this.blit(graphics, i, 2, 0, 0, 192, 192);
        if (this.cachedPage != this.currentPage) {
            FormattedText formattedtext = this.getPageRaw(this.currentPage);
            this.cachedPageComponents = this.font.split(formattedtext, 114);
            this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage
                    + 1,
                    Math.max(this.getNumPages(), 1));
        }

        this.cachedPage = this.currentPage;
        int i1 = this.font.width(this.pageMsg);

        // this.font.draw(graphics, this.pageMsg, (float) (i - i1 + 192 - 44), 18.0F,
        // 0);

        graphics.drawString(font, this.pageMsg,
                (int) (i - i1 + 192 - 44), 18, 0, false);

        int k = Math.min(128 / 9, this.cachedPageComponents.size());

        for (int l = 0; l < k; ++l) {
            FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);

            // this.font.draw(graphics, formattedcharsequence, (float) (i + 36), (float) (32
            // + l * 9), 0);

            graphics.drawString(font, formattedcharsequence,
                    (int) (i + 36), (int) (32
                            + l * 9),
                    0, false);
        }

        Style style = this.getClickedComponentStyleAt((double) p_98283_, (double) p_98284_);
        if (style != null) {
            // this.renderComponentHoverEffect(graphics, style, p_98283_, p_98284_);

            graphics.renderComponentHoverEffect(font, style, p_98283_, p_98284_);
        }

        super.render(graphics, p_98283_, p_98284_, p_98285_);
    }

    public boolean mouseClicked(double p_98272_, double p_98273_, int p_98274_) {
        if (p_98274_ == 0) {
            Style style = this.getClickedComponentStyleAt(p_98272_, p_98273_);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        return super.mouseClicked(p_98272_, p_98273_, p_98274_);
    }

    public boolean handleComponentClicked(@Nullable Style style) {

        ClickEvent clickevent = style.getClickEvent();
        if (clickevent == null) {
            return false;
        } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String s = clickevent.getValue();

            try {
                int i = Integer.parseInt(s) - 1;
                return this.forcePage(i);
            } catch (Exception exception) {
                return false;
            }
        } else {
            boolean flag = super.handleComponentClicked(style);
            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.closeScreen();
            }

            return flag;
        }

    }

    protected void closeScreen() {
        this.minecraft.setScreen((Screen) null);
    }

    @Nullable
    public Style getClickedComponentStyleAt(double p_98269_, double p_98270_) {
        if (this.cachedPageComponents.isEmpty()) {
            return null;
        } else {
            int i = Mth.floor(p_98269_ - (double) ((this.width - 192) / 2) - 36.0D);
            int j = Mth.floor(p_98270_ - 2.0D - 30.0D);
            if (i >= 0 && j >= 0) {
                int k = Math.min(128 / 9, this.cachedPageComponents.size());
                if (i <= 114 && j < 9 * k + k) {
                    int l = j / 9;
                    if (l >= 0 && l < this.cachedPageComponents.size()) {
                        FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
                        return this.minecraft.font.getSplitter().componentStyleAtWidth(formattedcharsequence,
                                i);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    static List<String> loadPages(CompoundTag p_169695_) {
        Builder<String> builder = ImmutableList.builder();
        loadPages(p_169695_, builder::add);
        return builder.build();
    }

    public static void loadPages(CompoundTag p_169697_, Consumer<String> p_169698_) {
        ListTag listtag = p_169697_.getList("pages", 8).copy();
        IntFunction<String> intfunction;
        if (Minecraft.getInstance().isTextFilteringEnabled() &&
                p_169697_.contains("filtered_pages", 10)) {
            CompoundTag compoundtag = p_169697_.getCompound("filtered_pages");
            intfunction = (p_169702_) -> {
                String s = String.valueOf(p_169702_);
                return compoundtag.contains(s) ? compoundtag.getString(s) : listtag.getString(p_169702_);
            };
        } else {
            intfunction = listtag::getString;
        }

        for (int i = 0; i < listtag.size(); ++i) {
            p_169698_.accept(intfunction.apply(i));
        }

    }

    public FormattedText getPageRaw(int p_98325_) {
        String s = this.MESSAGES.get(p_98325_);

        try {
            FormattedText formattedtext = Component.Serializer.fromJson(s);
            if (formattedtext != null) {
                return formattedtext;
            }
        } catch (Exception exception) {
        }

        return FormattedText.of(s);
    }

}

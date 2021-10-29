/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatbox.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatbox.AdvancedChatBox;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import java.util.ArrayList;
import java.util.List;

public class GuiChatBoxConfig extends GuiConfigsBase {

    private final List<GuiConfigHandler.TabButton> buttons;

    public GuiChatBoxConfig(List<GuiConfigHandler.TabButton> buttons) {
        super(10, 80, AdvancedChatBox.MOD_ID, null, "advancedchat.screen.main");
        this.buttons = buttons;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        int rows = 1;

        for (GuiConfigHandler.TabButton tab : buttons) {
            int newY = this.createButton(tab, y);
            if (newY != y) {
                rows++;
                y = newY;
            }
        }

        y += 22;
        x = width - 2;
        String name = ButtonListener.Type.CONFIG_FORMATTERS.getDisplayName();
        int w = StringUtils.getStringWidth(name) + 10;
        ButtonGeneric format = new ButtonGeneric(x - w, y, w, 20, name);
        this.addButton(format, new ButtonListener(ButtonListener.Type.CONFIG_FORMATTERS, this));
        x -= w + 2;
        name = ButtonListener.Type.CONFIG_SUGGESTORS.getDisplayName();
        w = StringUtils.getStringWidth(name) + 10;
        ButtonGeneric suggest = new ButtonGeneric(x - w, y, w, 20, name);
        this.addButton(suggest, new ButtonListener(ButtonListener.Type.CONFIG_SUGGESTORS, this));
        x -= w + 2;
        if (rows > 1) {
            int scrollbarPosition = this.getListWidget().getScrollbar().getValue();
            this.setListPosition(this.getListX(), 80 + (rows - 1) * 22);
            this.reCreateListWidget();
            this.getListWidget().getScrollbar().setValue(scrollbarPosition);
        } else {
            this.reCreateListWidget();
        }
        this.getListWidget().refreshEntries();
    }

    private int createButton(GuiConfigHandler.TabButton button, int y) {
        this.addButton(button.getButton(), new ButtonListenerConfigTabs(button));
        return button.getButton().getY();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<ConfigStorage.SaveableConfig<? extends IConfigBase>> configs =
                ChatBoxConfigStorage.General.OPTIONS;

        ArrayList<IConfigBase> config = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> s : configs) {
            config.add(s.config);
        }

        return ConfigOptionWrapper.createFor(config);
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final GuiChatBoxConfig parent;

        public ButtonListener(Type type, GuiChatBoxConfig parent) {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (type == Type.CONFIG_FORMATTERS) {
                GuiBase.openGui(new GuiFormatterRegistry(parent));
            } else if (type == Type.CONFIG_SUGGESTORS) {
                GuiBase.openGui(new GuiSuggestorRegistry(parent));
            }
        }

        public enum Type {
            CONFIG_FORMATTERS("advancedchatbox.config.button.config_formatters"),
            CONFIG_SUGGESTORS("advancedchatbox.config.button.config_suggestors");

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translationKey;
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }

    private static class ButtonListenerConfigTabs implements IButtonActionListener {

        private final GuiConfigHandler.TabButton tabButton;

        public ButtonListenerConfigTabs(GuiConfigHandler.TabButton tabButton) {
            this.tabButton = tabButton;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfigHandler.getInstance().activeTab = this.tabButton.getTab().getName();
            GuiBase.openGui(
                    this.tabButton.getTab().getScreen(GuiConfigHandler.getInstance().getButtons()));
        }
    }
}

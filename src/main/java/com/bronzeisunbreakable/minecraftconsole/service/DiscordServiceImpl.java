package com.bronzeisunbreakable.minecraftconsole.service;

import com.zgamelogic.annotations.Bot;
import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.session.ReadyEvent;

@DiscordController
@Slf4j
public class DiscordServiceImpl {
    /**
     * The @Bot annotation allows this field to be automatically populated by the JDA when it's logged in
     */
    @Bot
    private JDA bot;

    /**
     * Método que se ejecuta al estar ready el bot
     *
     * @param event Evento triggereado al estar ready
     */
    @DiscordMapping
    private void onReady(ReadyEvent event) {
        log.info("Bot {} has finished loading", event.getJDA().getSelfUser().getName());
        bot.setAutoReconnect(true);
        bot.getPresence().setPresence(OnlineStatus.ONLINE, true);
    }
}
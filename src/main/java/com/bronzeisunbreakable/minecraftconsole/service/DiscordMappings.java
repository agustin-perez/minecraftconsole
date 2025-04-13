package com.bronzeisunbreakable.minecraftconsole.service;

import com.bronzeisunbreakable.minecraftconsole.exception.AuthenticationException;
import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import com.zgamelogic.annotations.EventProperty;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;

@Slf4j
@DiscordController
public class DiscordMappings {
    private final RconService rconService;

    @Autowired
    public DiscordMappings(RconService rconService) {
        this.rconService = rconService;
    }

    /**
     * Mappeo de comando slash "command" para ejecutar comandos RCON en el servidor de Minecraft
     *
     * @param event Evento que contiene el payload del comando en cuestión a ejecutar
     */
    @DiscordMapping(Id = "command")
    private void rconDiscordMapping(SlashCommandInteractionEvent event) throws AuthenticationException, IOException {
        String command = event.getOption("command").getAsString();
        try {
            //TODO: PASAR A UNA LISTA
            if (!command.equals("stop")) {
                event.reply(rconService.sendCommand(command)).queue();
            } else {
                event.reply("No se pueden ejecutar comandos administrativos 🧐").queue();
            }
        } catch (Exception e) {
            event.reply("Error conectando a minecraft ☠️: " + e.getMessage()).queue();
        }
    }

    /**
     * Método que genera autocomplete para el mapping management
     *
     * @param event CommandAutoCompleteInteractionEvent evento que autocompleta al seleccionar management
     */
    @DiscordMapping(Id = "management", FocusedOption = "action")
    private void managementAutocompleteDiscordMapping(CommandAutoCompleteInteractionEvent event) {
        List<String> actions = List.of("Reiniciar");
        String typedAction = event.getFocusedOption().getValue();
        List<Command.Choice> choices = actions.stream()
                .filter(option -> option.contains(typedAction))
                .map(option -> new Command.Choice(option, option)).toList();
        event.replyChoices(choices).queue();
    }

    /**
     * Método que genera autocomplete para el mapping management
     *
     * @param event CommandAutoCompleteInteractionEvent evento que autocompleta al seleccionar management
     */
    @DiscordMapping(Id = "whitelist", FocusedOption = "action")
    private void whitelistAutocompleteDiscordMapping(CommandAutoCompleteInteractionEvent event) {
        List<String> actions = List.of("add", "remove", "reload", "list");
        String typedAction = event.getFocusedOption().getValue();
        List<Command.Choice> choices = actions.stream()
                .filter(option -> option.contains(typedAction))
                .map(option -> new Command.Choice(option, option)).toList();
        event.replyChoices(choices).queue();
    }

    /**
     * Método que ejecuta acciones administrativas en el servidor
     *
     * @param event  Evento a procesar
     * @param action Acción a ejecutar
     */
    @DiscordMapping(Id = "management")
    private void managementDiscordMapping(SlashCommandInteractionEvent event, @EventProperty String action) {
        log.info("Action: {} fue seleccionada", action);
        if (action.equals("Reiniciar")) {
            try {
                rconService.sendCommand("stop");
                event.reply("Reiniciando server 🔁").queue();
            } catch (Exception e) {
                event.reply("Error al reiniciar el server ☠️: " + e.getMessage()).queue();
            }
        } else {
            event.reply("Acción desconocida 🤨").queue();
        }
    }

    /**
     * Método que ejecuta acciones de whitelist en el servidor
     *
     * @param event  Evento a procesar
     * @param action Acción a ejecutar
     */
    @DiscordMapping(Id = "whitelist")
    private void whitelistDiscordMapping(SlashCommandInteractionEvent event, @EventProperty String action) {
        log.info("Action: {} fue seleccionada", action);
        try {
            switch (action) {
                case "add": {
                    String nickname = event.getOption("nickname").getAsString();
                    if (rconService.whitelistAdd(nickname)) {
                        event.reply("El jugador \"" + nickname + "\" ya puede loguearse 💦").queue();
                    } else {
                        event.reply("El jugador \"" + nickname + "\" ya está en el server ⚠️💦").queue();
                    }
                    break;
                }
                case "remove": {
                    String nickname = event.getOption("nickname").getAsString();
                    if (rconService.whitelistRemove(nickname)) {
                        event.reply("Jugador \"" + nickname + "\" nukeado ☣️").queue();
                    } else {
                        event.reply("El jugador \"" + nickname + "\" no existe ☣️☠️").queue();
                    }
                    break;
                }
                case "reload": {
                    if (rconService.whitelistReload()) {
                        event.reply("Whitelist recargada manualmente 📝🤓").queue();
                    } else {
                        event.reply("Error al recargar whitelist 📝⚠️☣️").queue();
                    }
                    break;
                }
                case "list": {
                    event.reply(rconService.whitelistList()).queue();
                    break;
                }
            }
        } catch (Exception e) {
            event.reply("Error al procesar acción en la whitelist 📝⚠️☣️").queue();
        }
    }

    /**
     * Seteo inicial de comandos para mappings de Discord
     *
     * @return Lista de configs de comandos
     */
    @Bean
    private List<CommandData> generalCommands() {
        return List.of(
                Commands.message("test").setContexts(InteractionContextType.PRIVATE_CHANNEL),
                Commands.slash("command", "Ejecuta un comando en el servidor de Minecraft")
                        .addOption(OptionType.STRING, "command", "Ejecuta un comando", true),
                Commands.slash("whitelist", "Administra la whitelist del servidor")
                        .addOptions(new OptionData(OptionType.STRING,
                                "action",
                                "Acción a realizar en la whitelist", true, true))
                        .addOption(OptionType.STRING, "nickname",
                                "Nickanme a realizar acción en la whitelist", false),
                Commands.slash("management", "Administración del server")
                        .addOptions(new OptionData(OptionType.STRING,
                                "action",
                                "Acciones administrativas del server", true, true)
                        )
        );
    }
}

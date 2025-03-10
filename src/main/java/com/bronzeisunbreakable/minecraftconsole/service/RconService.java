package com.bronzeisunbreakable.minecraftconsole.service;

import com.bronzeisunbreakable.minecraftconsole.Exception.AuthenticationException;
import com.bronzeisunbreakable.minecraftconsole.config.RconConfig;
import com.bronzeisunbreakable.minecraftconsole.rcon.Rcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RconService {
    private final RconConfig rconConfig;

    @Autowired
    public RconService(RconConfig rconConfig) {
        this.rconConfig = rconConfig;
    }

    /**
     * Función que ejecuta un comando y devuelve su ejecución en el servidor de Minecraft, a través de RCON
     *
     * @param command Comando a ser ejecutado
     * @return Command Resultado de la ejecución
     * @throws AuthenticationException Si la autenticación de RCON falla
     * @throws IOException             Si ocurre un error al ejecutar el comando
     */
    public String sendCommand(String command) throws AuthenticationException, IOException {
        log.info("Sending RCON command: {}", command);
        Rcon rcon = new Rcon(rconConfig.getHost(), rconConfig.getPort(), rconConfig.getPassword().getBytes());
        String response = rcon.command(command);
        rcon.disconnect();
        log.info("RCON response: {}", response);
        if (response.isEmpty()) {
            return "Comando sin respuesta";
        }
        return response;
    }

    /**
     * Función que agrega un jugador a la whitelist
     *
     * @param nickname Jugador a agregar
     * @return true si se ejecutó correctamente
     */
    public boolean whitelistAdd(String nickname) throws AuthenticationException, IOException {
        return !sendCommand("wl add " + nickname).equalsIgnoreCase("Player is already whitelisted\n");
    }

    /**
     * Función que elimina un jugador de la whitelist
     *
     * @param nickname Jugador a remover
     * @return true si se ejecutó correctamente
     */
    public boolean whitelistRemove(String nickname) throws AuthenticationException, IOException {
        return !sendCommand("wl remove " + nickname).equalsIgnoreCase("Player is not whitelisted\n");
    }

    /**
     * Función que recarga la whitelist
     *
     * @return true si se ejecutó correctamente
     */
    public boolean whitelistReload() throws AuthenticationException, IOException {
        return sendCommand("whitelist reload").equalsIgnoreCase("Reloaded the whitelist\n");
    }

    /**
     * Función que ejecuta comando whitelist list
     *
     * @return true si se ejecutó correctamente
     */
    public String whitelistList() throws AuthenticationException, IOException {
        return sendCommand("whitelist list");
    }
}
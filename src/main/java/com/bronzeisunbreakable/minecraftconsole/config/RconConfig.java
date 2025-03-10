package com.bronzeisunbreakable.minecraftconsole.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("rcon-settings")
public class RconConfig {
    private String host;
    private int port;
    private String password;
}

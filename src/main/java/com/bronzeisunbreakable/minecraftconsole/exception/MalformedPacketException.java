package com.bronzeisunbreakable.minecraftconsole.exception;

import java.io.IOException;

public class MalformedPacketException extends IOException {
    public MalformedPacketException(String message) {
        super(message);
    }
}
package com.saum.protocol.response;

import com.saum.protocol.Command;
import com.saum.protocol.Packet;
import lombok.Data;

/**
 * @Author saum
 * @Description:
 */
@Data
public class LogoutResponsePacket extends Packet {
    private boolean success;

    private String reason;

    @Override
    public Byte getCommandType() {
        return Command.LOGOUT_RESPONSE;
    }
}

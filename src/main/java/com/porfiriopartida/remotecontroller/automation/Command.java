package com.porfiriopartida.remotecontroller.automation;

import org.springframework.util.StringUtils;

public enum Command {
    CLICK,
    MOVE
    ;

    public static Command DEFAULT = CLICK;

    public static Command parse(String cmd){
        if(StringUtils.isEmpty(cmd)){
            return Command.DEFAULT;
        }
        return Command.valueOf(cmd.toUpperCase());
    }
}

package com.porfiriopartida.remotecontroller.swgoh;

import com.porfiriopartida.remotecontroller.web.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping(value = "/swgoh/cards")
public class SwgohEndpoints {
    @RequestMapping(value = "/resume")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse collect(){
        String status = "Bot started";
        CustomResponse swgohResponse = new CustomResponse();
        swgohResponse.setStatus(status);

        ClickBot.getInstance().resume();

        return swgohResponse;
    }
    @RequestMapping(value = "/stopRun")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse stop(){
        String status = "Bot stopped";
        CustomResponse swgohResponse = new CustomResponse();
        swgohResponse.setStatus(status);

        ClickBot.getInstance().stop();

        return swgohResponse;
    }
}

package com.porfiriopartida.remotecontroller.swgoh;

import com.porfiriopartida.remotecontroller.web.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Deprecated
//@RestController
//@RequestMapping(value = "/swgoh/cards")
public class SwgohEndpoints {
    @RequestMapping(value = "/resume")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse collect(HttpServletRequest request, HttpServletResponse response, List<Camisa>  inv){
        String status = "Bot started";
        CustomResponse swgohResponse = new CustomResponse();
        swgohResponse.setStatus(status);

        ClickBot.getInstance().resume();

        List<Camisa> sub = new ArrayList<>();
        String color = "";
        for(Camisa camisa:  inv){
            if( camisa.getColor().equals(color) ) {
                sub.add(camisa);
            }
        }


        return swgohResponse;
    }
    class Camisa{
        public String getColor(){
            return "";
        }
    }
    @RequestMapping(value = "/stop")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse stop(HttpServletRequest request, HttpServletResponse response){
        String status = "Bot stopped";
        CustomResponse swgohResponse = new CustomResponse();
        swgohResponse.setStatus(status);

        ClickBot.getInstance().stop();

        return swgohResponse;
    }
}

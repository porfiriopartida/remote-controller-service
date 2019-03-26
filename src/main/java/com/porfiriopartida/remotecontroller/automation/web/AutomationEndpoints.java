package com.porfiriopartida.remotecontroller.automation.web;

import com.porfiriopartida.remotecontroller.automation.AutomationConfigurationHandler;
import com.porfiriopartida.remotecontroller.web.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping(value = "/automation")
public class AutomationEndpoints {
    @Autowired
    private AutomationConfigurationHandler automationHandler;

    @RequestMapping(value = "/{namespace}/{test}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse runTestCase(@PathVariable(name = "namespace") String namespace, @PathVariable(name = "test") String testCase) throws FileNotFoundException {
        automationHandler.runTestCase(namespace, testCase);


        String status = "Starting swgoh.";

        CustomResponse customResponse = new CustomResponse();
        customResponse.setStatus(status);

        return customResponse;
    }
}

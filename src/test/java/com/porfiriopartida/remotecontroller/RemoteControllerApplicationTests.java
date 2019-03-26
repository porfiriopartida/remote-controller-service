package com.porfiriopartida.remotecontroller;

import com.porfiriopartida.remotecontroller.automation.AutomationConfigurationHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RemoteControllerApplicationTests {

	@Autowired
	private AutomationConfigurationHandler handler;
	@Test
	public void contextLoads() {
		Assert.assertNotNull(handler);
	}

}

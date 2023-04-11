package com.shop.inventory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.shop.inventory.controller.DeviceController;

@SpringBootTest
@ActiveProfiles("integration")
class InventoryApplicationTests {

	@Autowired
	DeviceController deviceController;

	@Test
	void contextLoads() {
		assertNotNull(deviceController);
	}

}

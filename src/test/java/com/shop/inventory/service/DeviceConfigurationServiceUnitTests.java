package com.shop.inventory.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.transaction.annotation.Transactional;

import com.shop.inventory.InventoryApplication;
import com.shop.inventory.entity.Device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(scripts = { "classpath:sql-scripts/set-up.sql",
        "classpath:sql-scripts/create-devices-table.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "classpath:sql-scripts/drop-devices-table.sql",
        "classpath:sql-scripts/end.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)

@SpringBootTest(classes = InventoryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unit")
@SqlMergeMode(MergeMode.MERGE)
@Transactional
public class DeviceConfigurationServiceUnitTests {

    @Autowired
    DeviceConfigurationService deviceConfigurationService;

    // region Test CONFIGURE Method

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testConfigureMethodWithUnActiveDevice() {
        Device device = deviceConfigurationService.configure(1);
        assertNotNull(device);
        assertEquals(device.getStatus().toString(), "ACTIVE");
        assertTrue(device.getTemperature() >= 0 || device.getTemperature() <= 10);

    }

    @Test
    public void testConfigureMethodWithUnExistDevice() {
        assertThrows(IllegalStateException.class, () -> {
            deviceConfigurationService.configure(1);
        });

    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testConfigureMethodWithActiveDevice() {
        Device device = deviceConfigurationService.configure(1);
        assertNotNull(device);
        assertEquals(device.getStatus().toString(), "ACTIVE");
        assertTrue(device.getTemperature() >= 0 || device.getTemperature() <= 10);

    }

    // endregion
}

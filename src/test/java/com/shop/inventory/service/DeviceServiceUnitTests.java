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
import com.shop.inventory.dto.DeviceDTO;
import com.shop.inventory.entity.Device;
import com.shop.inventory.repository.DeviceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;;

@Sql(scripts = { "classpath:sql-scripts/set-up.sql",
        "classpath:sql-scripts/create-devices-table.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "classpath:sql-scripts/drop-devices-table.sql",
        "classpath:sql-scripts/end.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)

@SpringBootTest(classes = InventoryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unit")
@SqlMergeMode(MergeMode.MERGE)
@Transactional(readOnly = false)
public class DeviceServiceUnitTests {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceRepository deviceRepository;

    // region DELETE Method
    @Test
    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteDeviceSuccess() {
        deviceService.delete(1L);
        assertEquals(deviceRepository.count(), 0);
    }

    @Test
    public void testDeleteDeviceWithNonExistingId() {
        deviceService.delete(1L);
        assertEquals(deviceRepository.count(), 0);

    }

    // endregion
    // region ADD Method

    @Test
    public void testAddDeviceSuccess() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("1234567");
        deviceService.add(deviceDTO);
        assertEquals(deviceRepository.count(), 1);
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testAddDeviceWithExistingPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("1478965");
        assertThrows(IllegalStateException.class, () -> {
            deviceService.add(deviceDTO);
        });
    }

    @Test
    public void testAddDeviceWithNullPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();

        assertThrows(IllegalStateException.class, () -> {
            deviceService.add(deviceDTO);
        });
    }

    @Test
    public void testAddDeviceWithEmptyPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("");

        assertThrows(IllegalStateException.class, () -> {
            deviceService.add(deviceDTO);
        });
    }
    // endregion

    // region UPDATE Method

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testUpdateDeviceSuccess() {

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("1234567");
        deviceDTO.setTemperature(5);

        Device device = deviceService.update(1L, deviceDTO);

        assertNotNull(device);
        assertEquals(device.getPinCode(), "1234567");
        assertEquals(device.getTemperature(), 5);
    }

    @Test
    public void testUpdateDeviceWithNonExistingId() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("1234567");
        deviceDTO.setTemperature(5);

        assertThrows(IllegalStateException.class, () -> {
            deviceService.update(1L, deviceDTO);
        });
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-devices.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testUpdateDeviceWithExistingPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("0000001");
        deviceDTO.setTemperature(5);

        assertThrows(Exception.class, () -> {
            deviceService.update(1L, deviceDTO);
        });
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testUpdateDeviceWithNullPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode(null);
        deviceDTO.setTemperature(3);

        Device device = deviceService.update(1L, deviceDTO);

        assertNotNull(device);
        assertEquals(device.getPinCode(), "1478965");
        assertEquals(device.getTemperature(), 3);
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testUpdateDeviceWithEmptyPinCode() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("");
        deviceDTO.setTemperature(3);

        Device device = deviceService.update(1L, deviceDTO);

        assertNotNull(device);
        assertEquals(device.getPinCode(), "1478965");
        assertEquals(device.getTemperature(), 3);
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testUpdateDeviceWithoutTemperature() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setPinCode("1234567");

        Device device = deviceService.update(1L, deviceDTO);

        assertNotNull(device);
        assertEquals(device.getPinCode(), "1234567");
        assertEquals(device.getTemperature(), -1);
    }
    // endregion

    // region GET Method
    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-devices.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testGetActiveDeviceSuccess() {

        Collection<Device> devices = deviceService.getDevicesForSale();
        assertEquals(devices.size(), 5);
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testGetActiveDeviceWithOnlyUnActiveDevices() {

        Collection<Device> devices = deviceService.getDevicesForSale();
        assertEquals(devices.size(), 0);
    }
    // end region

}

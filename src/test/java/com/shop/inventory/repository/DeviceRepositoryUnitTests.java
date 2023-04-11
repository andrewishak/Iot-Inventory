package com.shop.inventory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

import com.shop.inventory.entity.Device;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Sql(scripts = { "classpath:sql-scripts/set-up.sql",
        "classpath:sql-scripts/create-devices-table.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "classpath:sql-scripts/drop-devices-table.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)

@ActiveProfiles("unit")
@SqlMergeMode(MergeMode.MERGE)
@DataJpaTest
public class DeviceRepositoryUnitTests {

    @Autowired
    DeviceRepository deviceRepository;

    // region Test FIND BY PIN CODE Method
    @Test
    public void testNoDataExists() {
        assertEquals(deviceRepository.count(), 0);
    }

    @Test
    public void testFindByPinCodeWhenPinCodeNotExists() {
        final String pinCode = "1234567";
        Optional<Device> result = deviceRepository.findByPinCode(pinCode);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByPinCodeWhenPinCodeExists() {

        final String pinCode = "1234567";
        Device device = new Device(pinCode);
        deviceRepository.save(device);

        Optional<Device> result = deviceRepository.findByPinCode(pinCode);
        assertTrue(result.isPresent());
        assertEquals(device, result.get());
    }

    @Test
    void testFindByPinCodeWhenPinCodeIsNull() {

        Optional<Device> result = deviceRepository.findByPinCode(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByPinCodeWhenPinCodeIsEmpty() {

        Optional<Device> result = deviceRepository.findByPinCode("");
        assertTrue(result.isEmpty());
    }

    // endregion

    // region Test FIND ALL ACTIVE DEVICES ORDERED BY PIN CODE Method

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    void testFindAllActiveDevicesOrderedByPinCodeWhenOnlyOneActiveDeviceExists() {

        Collection<Device> result = deviceRepository.findAllActiveDevicesOrderedByPinCode();

        assertEquals(1, result.size());
        assertEquals("1234567", result.iterator().next().getPinCode());
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-devices.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    void testFindAllActiveDevicesOrderedByPinCodeWhenMultipleActiveDevicesExist() {

        Collection<Device> result = deviceRepository.findAllActiveDevicesOrderedByPinCode();

        assertEquals(5, result.size());
        Iterator<Device> itr = result.iterator();
        assertEquals("0000001", itr.next().getPinCode());
        assertEquals("0000010", itr.next().getPinCode());
        assertEquals("0000100", itr.next().getPinCode());
        assertEquals("0001000", itr.next().getPinCode());
        assertEquals("0010000", itr.next().getPinCode());

    }
    // endregion

}
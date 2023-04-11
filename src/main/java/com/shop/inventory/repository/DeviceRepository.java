package com.shop.inventory.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shop.inventory.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByPinCode(String pinCode);

    @Query(value = "SELECT * FROM devices d Where d.status = 'ACTIVE' ORDER BY d.pin_code", nativeQuery = true)
    Collection<Device> findAllActiveDevicesOrderedByPinCode();

}
package com.shop.inventory.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.inventory.entity.Device;
import com.shop.inventory.enumClass.Status;
import com.shop.inventory.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceConfigurationServiceImpl implements DeviceConfigurationService {

    private final DeviceRepository deviceRepository;

    public Device configure(long deviceId) {

        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            throw new IllegalStateException("Could not find device with id = " + deviceId);
        }
        Device deviceObj = device.get();
        deviceObj.setStatus(Status.ACTIVE);
        deviceObj.setTemperature((int) (10 * Math.random()));
        deviceRepository.save(deviceObj);
        return deviceObj;

    }

}

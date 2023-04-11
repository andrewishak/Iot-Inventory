package com.shop.inventory.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import java.util.Collection;

import com.shop.inventory.dto.DeviceDTO;
import com.shop.inventory.entity.Device;
import com.shop.inventory.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    public void add(DeviceDTO deviceDTO) {
        final String pinCode = deviceDTO.getPinCode();
        if (pinCode == null || pinCode.isBlank()) {
            throw new IllegalStateException("Device pin_code is required.");
        }
        if (deviceRepository.findByPinCode(pinCode).isPresent()) {
            throw new IllegalStateException("Device pin_code (" + pinCode + ") is already exists.");
        }
        deviceRepository.save(new Device(pinCode));
    }

    @Override
    public void delete(long deviceId) {
        deviceRepository.deleteById(deviceId);
    }

    @Override
    public Device update(long deviceId, DeviceDTO deviceDTO) {

        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            throw new IllegalStateException("Could not find device with id= " + deviceId);
        }
        Device deviceObj = device.get();

        String pinCode = deviceDTO.getPinCode();

        if (pinCode != null && !pinCode.isBlank()) {
            Optional<Device> optionalDevice = deviceRepository.findByPinCode(pinCode);
            if (optionalDevice.isPresent() && (optionalDevice.get().getId() != deviceObj.getId())) {
                throw new IllegalStateException("Device pin_code (" + pinCode + ") is already exists.");
            }
            deviceObj.setPinCode(pinCode);
        }

        Integer temp = deviceDTO.getTemperature();
        if (temp != null) {
            deviceObj.setTemperature(temp);
        }

        deviceRepository.save(deviceObj);
        return deviceObj;
    }

    @Override
    public Collection<Device> getDevicesForSale() {
        return deviceRepository.findAllActiveDevicesOrderedByPinCode();
    }

}

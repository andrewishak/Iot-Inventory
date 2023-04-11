package com.shop.inventory.service;

import java.util.Collection;

import com.shop.inventory.dto.DeviceDTO;
import com.shop.inventory.entity.Device;

public interface DeviceService {

    void add(DeviceDTO deviceDTO);

    void delete(long deviceId);

    Device update(long deviceId, DeviceDTO deviceDTO);

    Collection<Device> getDevicesForSale();

}

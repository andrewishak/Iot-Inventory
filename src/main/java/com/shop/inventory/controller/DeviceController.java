package com.shop.inventory.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.shop.inventory.dto.DeviceDTO;
import com.shop.inventory.entity.Device;
import com.shop.inventory.service.DeviceConfigurationService;
import com.shop.inventory.service.DeviceService;
import com.shop.inventory.validationGroups.DeviceCreate;
import com.shop.inventory.validationGroups.DeviceUpdate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/inventory/device")
@RequiredArgsConstructor
@Validated
public class DeviceController {

	@Autowired
	DeviceService deviceService;

	@Autowired
	DeviceConfigurationService deviceConfigurationService;

	@GetMapping()
	public ResponseEntity<?> getDevicesForSale() {
		Collection<Device> devices = deviceService.getDevicesForSale();
		return new ResponseEntity<>(devices, HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<?> add(@Validated(DeviceCreate.class) @RequestBody DeviceDTO deviceDTO) {
		deviceService.add(deviceDTO);
		HashMap<String, String> map = new HashMap<>();
		map.put("Message", "Created Successfully");
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") long deviceId,
			@Validated(DeviceUpdate.class) @RequestBody DeviceDTO deviceDTO) {
		Device device = deviceService.update(deviceId, deviceDTO);
		return new ResponseEntity<>(device, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") long deviceId) {
		deviceService.delete(deviceId);
		HashMap<String, String> map = new HashMap<>();
		map.put("Message", "Deleted Successfully");
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@PostMapping("/{id}/configure")
	public ResponseEntity<?> configure(@PathVariable("id") long deviceId) {
		Device device = deviceConfigurationService.configure(deviceId);
		return new ResponseEntity<>(device, HttpStatus.OK);
	}

}
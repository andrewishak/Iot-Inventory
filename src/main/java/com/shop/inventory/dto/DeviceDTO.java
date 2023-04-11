package com.shop.inventory.dto;

import com.shop.inventory.validationGroups.DeviceCreate;
import com.shop.inventory.validationGroups.DeviceUpdate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeviceDTO {

    @NotBlank(message = "Device pin_code is required and mustn't be blank.", groups = { DeviceCreate.class })
    @Pattern(regexp = "^[0-9]{7}$", message = "Device pin_code must be a 7 digits length.", groups = {
            DeviceUpdate.class, DeviceCreate.class })
    private String pinCode;

    @Min(value = 0, groups = DeviceUpdate.class)
    @Max(value = 10, groups = DeviceUpdate.class)
    private Integer temperature;

}

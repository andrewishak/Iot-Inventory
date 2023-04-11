package com.shop.inventory.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.shop.inventory.InventoryApplication;

import jakarta.transaction.Transactional;

@SpringBootTest(classes = InventoryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@SqlMergeMode(MergeMode.MERGE)
@Transactional
@Sql(scripts = { "classpath:sql-scripts/set-up.sql",
        "classpath:sql-scripts/create-devices-table.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "classpath:sql-scripts/drop-devices-table.sql",
        "classpath:sql-scripts/end.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)

public class DeviceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddDeviceSuccess() throws Exception {

        var content = """
                      {
                        "pin_code": "1234567"
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/inventory/device")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("Created Successfully"));

    }

    @ParameterizedTest
    @CsvSource({ """
            "12345", Device pin_code must be a 7 digits length.""", """
            "12345678", Device pin_code must be a 7 digits length.""", """
            "ddddddd", Device pin_code must be a 7 digits length.""", """
            null, Device pin_code is required and mustn't be blank.""", """
            "", Device pin_code is required and mustn't be blank."""
    })
    void testAddDeviceValidation(final String pinCode, final String error) throws Exception {

        var content = """
                {
                    "pin_code": %s
                }""".formatted(pinCode);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/inventory/device")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("pinCode").value(error));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testAddDeviceWithPinCodeExists() throws Exception {

        var content = """
                      {
                        "pin_code": "1478965"
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/inventory/device")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("Error").value("Device pin_code (1478965) is already exists."));

    }

    @Test
    void testDeleteNotExistsDevice() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("Deleted Successfully"));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testDeleteDevice() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("Message").value("Deleted Successfully"));
    }

    @Test
    void testConfigureNotExistsDevice() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/inventory/device/1/configure")
                .accept(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("Error").value("Could not find device with id = 1"));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-unactive-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testConfigureDevice() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/inventory/device/1/configure")
                .accept(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pin_code").value("1478965"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("temperature").isNumber());
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testUpdateDevicePinCodeSuccess() throws Exception {

        var content = """
                      {
                        "pin_code": "1234568"
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("pin_code").value("1234568"));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testUpdateDeviceTemperatureSuccess() throws Exception {

        var content = """
                      {
                        "temperature": 2
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("temperature").value(2));
    }

    @Test
    void testUpdateDeviceNotExist() throws Exception {

        var content = """
                      {
                        "temperature": 2
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("Error").value("Could not find device with id= 1"));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @ParameterizedTest
    @CsvSource({ """
            "12345",5,pinCode, Device pin_code must be a 7 digits length.""", """
            "12345678",5,pinCode, Device pin_code must be a 7 digits length.""", """
            "ddddddd",5,pinCode, Device pin_code must be a 7 digits length.""", """
            "1234567",-1,temperature, must be greater than or equal to 0""", """
            "1234567",11,temperature, must be less than or equal to 10"""
    })
    void testUpdateDeviceValidation(final String pinCode, final int temp, final String field, final String error)
            throws Exception {

        var content = """
                {
                    "pin_code": %s,
                    "temperature" : %d
                }""".formatted(pinCode, temp);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath(field).value(error));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-devices.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    void testUpdateDeviceWithExistingPinCode() throws Exception {

        var content = """
                      {
                        "pin_code": "0000100"
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("Error").value("Device pin_code (0000100) is already exists."));
    }

    @Sql(scripts = {
            "classpath:sql-scripts/insert-active-device.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testUpdateDevicePinCodeWithSameValue() throws Exception {

        var content = """
                      {
                        "pin_code": "1234567"
                      }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/v1/inventory/device/1")
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions response = mockMvc.perform(requestBuilder);
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("pin_code").value("1234567"));
    }

}

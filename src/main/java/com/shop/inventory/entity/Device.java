package com.shop.inventory.entity;

import com.shop.inventory.enumClass.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
public class Device {

    // #region Properties

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pin_code", columnDefinition = "bpchar")
    private String pinCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "temperature")
    private Integer temperature;
    // #endregion

    // #region Constructors

    public Device(String pinCode) {
        this.setPinCode(pinCode);
        this.setStatus(Status.READY);
        this.setTemperature(-1);
    }

    // #endregion
}

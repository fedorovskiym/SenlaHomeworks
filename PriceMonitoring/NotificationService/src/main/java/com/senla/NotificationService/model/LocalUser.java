package com.senla.NotificationService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "local_users")
public class LocalUser {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "phoneNumber")
    private String phoneNumber;

    public LocalUser(UUID id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public LocalUser() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LocalUser localUser = (LocalUser) o;
        return Objects.equals(id, localUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "LocalUser{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

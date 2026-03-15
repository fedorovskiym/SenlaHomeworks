package com.senla.consumer.model;

import com.senla.consumer.model.enums.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;
    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;
    @Column(name = "amount")
    private Integer amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status")
    private TransferStatus transferStatus;

    public Transfer() {
    }

    public Transfer(Long id, Account fromAccount, Account targetAccount, Integer amount) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", fromId=" + fromAccount +
                ", targetId=" + targetAccount +
                ", amount=" + amount +
                '}';
    }
}

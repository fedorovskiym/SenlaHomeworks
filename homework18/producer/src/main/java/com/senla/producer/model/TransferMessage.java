package com.senla.producer.model;

public class TransferMessage {
    private static Long count = 0L;
    private Long id;
    private Long fromAccount;
    private Long targetAccount;
    private Integer amount;

    public TransferMessage(Long fromAccount, Long targetAccount, Integer amount) {
        this.fromAccount = fromAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.id = count;
        count++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Long targetAccount) {
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
        return "TransferMessage{" +
                "id=" + id +
                ", fromAccount=" + fromAccount +
                ", targetAccount=" + targetAccount +
                ", amount=" + amount +
                '}';
    }
}

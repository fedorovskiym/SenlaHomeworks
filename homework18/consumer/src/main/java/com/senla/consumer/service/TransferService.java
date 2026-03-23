package com.senla.consumer.service;

import com.senla.consumer.dto.TransferDTO;
import com.senla.consumer.model.Account;
import com.senla.consumer.model.Transfer;
import com.senla.consumer.model.enums.TransferStatus;
import com.senla.consumer.repository.TransferRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountService accountService;
    private final Logger logger = LogManager.getLogger(TransferService.class);

    @Autowired
    public TransferService(TransferRepository transferRepository, AccountService accountService) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    @Transactional
    public void doTransfer(TransferDTO transferDTO) {

        Account fromAccount = accountService.findById(transferDTO.fromAccount());
        if(!checkIsAccountExists(fromAccount, transferDTO.fromAccount())) {
            return;
        }

        if (fromAccount.getBalance() < transferDTO.amount()) {
            logger.error("На счете № {} не хватает средств", transferDTO.fromAccount());
            return;
        }

        Account targetAccount = accountService.findById(transferDTO.targetAccount());
        if(!checkIsAccountExists(targetAccount, transferDTO.targetAccount())) {
            return;
        }

        Transfer transfer = new Transfer(fromAccount, targetAccount, transferDTO.amount());
        try {
            fromAccount.setBalance(fromAccount.getBalance() - transferDTO.amount());
            targetAccount.setBalance(targetAccount.getBalance() + transferDTO.amount());
            transfer.setTransferStatus(TransferStatus.DONE);
            transferRepository.save(transfer);
            logger.info("Транзакция прошла успешно");
        } catch (Exception e) {
            transfer.setTransferStatus(TransferStatus.FINISHED_WITH_EXCEPTION);
            transferRepository.save(transfer);
            logger.error("Транзакция завершилась с ошибкой");
        }
        accountService.updateAccount(fromAccount);
        accountService.updateAccount(targetAccount);
    }

    private boolean checkIsAccountExists(Account account, Long id) {
        if (account == null) {
            logger.error("Аккаунт с id {} не существует", id);
            return false;
        }
        return true;
    }
}

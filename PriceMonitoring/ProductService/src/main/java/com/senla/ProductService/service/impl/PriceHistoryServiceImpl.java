package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryDataDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import com.senla.ProductService.service.PriceHistoryService;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public PriceHistoryServiceImpl(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional
    public void save(PriceHistory priceHistory) {
        priceHistoryRepository.save(priceHistory);
    }

    @Override
    @Transactional
    public void saveList(List<PriceHistory> priceHistoryList) {
        priceHistoryList.forEach(priceHistoryRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public PriceHistoryDTO getCoordsForChart(Long productId, Long shopBranchId) {
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAllByProductIdAndShopBranchId(productId, shopBranchId);
        if (priceHistoryList.isEmpty()) {
            return new PriceHistoryDTO(null, productId, null, shopBranchId, null, List.of());
        }

        List<PriceHistoryDataDTO> priceHistoryDataDTOList = priceHistoryList.stream()
                .map(priceHistory -> new PriceHistoryDataDTO(
                        priceHistory.getOldPrice(),
                        priceHistory.getNewPrice(),
                        priceHistory.getChangeDate()
                ))
                .toList();

        PriceHistoryDTO priceHistoryDTO = new PriceHistoryDTO(priceHistoryList.get(0).getId(), priceHistoryList.get(0).getProduct().getId(),
                priceHistoryList.get(0).getProduct().getName(), priceHistoryList.get(0).getShopBranch().getId(),
                priceHistoryList.get(0).getShopBranch().getShop().getName(), priceHistoryDataDTOList);

        return priceHistoryDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCsv(PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO) {
        if(periodOfTimeDTO.endDate().isBefore(periodOfTimeDTO.startDate())) {
            throw new InvalidParameterException("End date should be before start date!");
        }

        List<PriceHistory> priceHistoryList = priceHistoryRepository.findOverPeriodOfTime(periodOfTimeDTO.productId(),
                periodOfTimeDTO.shopBranchId(), periodOfTimeDTO.startDate(), periodOfTimeDTO.endDate());

        if (priceHistoryList.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("id;oldPrice;newPrice;changeDate;").append(System.lineSeparator());
        priceHistoryList.forEach(priceHistory ->
                stringBuilder.append(String.format("%d;%.2f;%.2f;%s",
                                priceHistory.getId(),
                                priceHistory.getOldPrice(),
                                priceHistory.getNewPrice(),
                                priceHistory.getChangeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .append(System.lineSeparator()));

        return stringBuilder.toString();
    }

}

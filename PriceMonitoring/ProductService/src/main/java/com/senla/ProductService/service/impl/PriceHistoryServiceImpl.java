package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryDataDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import com.senla.ProductService.service.PriceHistoryService;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(PriceHistoryServiceImpl.class);

    @Autowired
    public PriceHistoryServiceImpl(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional
    public void save(PriceHistory priceHistory) {
        logger.info("Save price history {}", priceHistory);
        priceHistoryRepository.save(priceHistory);
        logger.info("Successfully save price history {}", priceHistory);
    }

    @Override
    @Transactional
    public void saveList(List<PriceHistory> priceHistoryList) {
        logger.info("Save price history list");
        priceHistoryList.forEach(priceHistoryRepository::save);
        logger.info("Successfully save price history list");
    }

    @Override
    @Transactional(readOnly = true)
    public PriceHistoryDTO getCoordsForChart(UUID productId, UUID shopBranchId) {
        logger.info("Get coords for chart by shopBranchId {}", shopBranchId);
        List<PriceHistory> priceHistoryList =
                priceHistoryRepository.findAllByProductIdAndShopBranchId(productId, shopBranchId);
        if (priceHistoryList.isEmpty()) {
            logger.info("Price history list by shopBranchId {} is empty", shopBranchId);
            return new PriceHistoryDTO(null, productId, null, shopBranchId, null, List.of());
        }

        List<PriceHistoryDataDTO> priceHistoryDataDTOList = priceHistoryList.stream()
                .map(priceHistory -> new PriceHistoryDataDTO(
                        priceHistory.getOldPrice(),
                        priceHistory.getNewPrice(),
                        priceHistory.getChangeDate()
                ))
                .toList();

        logger.info("Successfully retrieve price history by shopBranchId {}", shopBranchId);
        return buildPriceHistoryDTO(priceHistoryList, priceHistoryDataDTOList);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCsv(PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO) {
        logger.info("Generate csv with price history over period of time {}", periodOfTimeDTO);
        if (periodOfTimeDTO.endDate().isBefore(periodOfTimeDTO.startDate())) {
            logger.warn("End date {} is before start date {}", periodOfTimeDTO.endDate(), periodOfTimeDTO.startDate());
            throw new InvalidParameterException("End date should be before start date!");
        }

        List<PriceHistory> priceHistoryList = priceHistoryRepository.findOverPeriodOfTime(periodOfTimeDTO.productId(),
                periodOfTimeDTO.shopBranchId(), periodOfTimeDTO.startDate(), periodOfTimeDTO.endDate());

        if (priceHistoryList.isEmpty()) {
            logger.info("Price history list is empty");
            return "";
        }

        return buildCsv(priceHistoryList, periodOfTimeDTO);
    }

    private String buildCsv(List<PriceHistory> priceHistoryList,
                            PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("id;oldPrice;newPrice;changeDate;").append(System.lineSeparator());
        priceHistoryList.forEach(priceHistory ->
                stringBuilder.append(String.format("%s;%.2f;%.2f;%s",
                                priceHistory.getId(),
                                priceHistory.getOldPrice(),
                                priceHistory.getNewPrice(),
                                priceHistory.getChangeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .append(System.lineSeparator()));
        logger.info("Successfully find price history over period of time {}", periodOfTimeDTO);
        return stringBuilder.toString();
    }

    private PriceHistoryDTO buildPriceHistoryDTO(List<PriceHistory> priceHistoryList,
                                                 List<PriceHistoryDataDTO> priceHistoryDataDTOList) {
        return new PriceHistoryDTO(priceHistoryList.get(0).getId(), priceHistoryList.get(0).getProduct().getId(),
                priceHistoryList.get(0).getProduct().getName(), priceHistoryList.get(0).getShopBranch().getId(),
                priceHistoryList.get(0).getShopBranch().getShop().getName(), priceHistoryDataDTOList);
    }
}

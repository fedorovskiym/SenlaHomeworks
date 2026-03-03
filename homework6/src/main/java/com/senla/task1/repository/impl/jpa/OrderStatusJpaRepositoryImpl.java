package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderStatusRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderStatusJpaRepositoryImpl extends AbstractJpaRepository<OrderStatus, Integer> implements OrderStatusRepository {

    private static final String HQL_FIND_BY_CODE = """
            SELECT o from OrderStatus o WHERE code = :code
            """;

    public OrderStatusJpaRepositoryImpl(Class<OrderStatus> type) {
        super(type);
    }

    public OrderStatusJpaRepositoryImpl() {
        super(OrderStatus.class);
    }

    @Override
    public Optional<OrderStatus> findByCode(OrderStatusType code) {
        EntityManager em = getEntityManager();
        Optional<OrderStatus> orderStatus;
        orderStatus = Optional.ofNullable(em.createQuery(HQL_FIND_BY_CODE, OrderStatus.class)
                .setParameter("code", code)
                .getSingleResult());
        return orderStatus;
    }

    @Override
    public Optional<OrderStatus> findByCodeString(String code) {
        EntityManager em = getEntityManager();
        OrderStatusType orderStatusType = OrderStatusType.valueOf(code.trim().toUpperCase());
        Optional<OrderStatus> orderStatus;
        orderStatus = Optional.ofNullable(em.createQuery(HQL_FIND_BY_CODE, OrderStatus.class)
                .setParameter("code", orderStatusType)
                .getSingleResult());
        return orderStatus;
    }

    @Override
    public Boolean checkIsOrderStatusExists(Integer id) {
        EntityManager em = getEntityManager();
        if (em.find(OrderStatus.class, id) == null) {
            return false;
        }
        return true;
    }
}

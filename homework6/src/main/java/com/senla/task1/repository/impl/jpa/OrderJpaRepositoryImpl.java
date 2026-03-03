package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderRepository;
import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("orderJpaDAO")
public class OrderJpaRepositoryImpl extends AbstractJpaRepository<Order, Integer> implements OrderRepository {

    private static final String HQL_SORT_BY = """
            SELECT o FROM Order o ORDER BY
            """;
    private static final String HQL_FIND_BY_MECHANIC = """
            SELECT o FROM Order o WHERE o.mechanic.id = :mechanicId
            """;
    private static final String HQL_FIND_BY_STATUS = """
            SELECT o FROM Order o WHERE o.status = :status
            """;
    private static final String HQL_PERIOD_OF_TIME = """
            SELECT o FROM Order o WHERE submissionDateTime BETWEEN :start
                        AND :end ORDER BY
            """;
    private static final String HQL_END_DATE_TIME = """
            SELECT o FROM Order o INNER JOIN o.status s WHERE s.code = :waiting OR s.code = :accepted
            ORDER BY o.submissionDateTime DESC
            """;

    public OrderJpaRepositoryImpl(Class<Order> type) {
        super(type);
    }

    public OrderJpaRepositoryImpl() {
        super(Order.class);
    }

    @Override
    public Optional<Order> getEndDateTimeLastActiveOrder() {
        EntityManager em = getEntityManager();
        Optional<Order> order;
        order = Optional.ofNullable(em.createQuery(HQL_END_DATE_TIME, Order.class)
                .setParameter("waiting", OrderStatusType.WAITING)
                .setParameter("accepted", OrderStatusType.ACCEPTED)
                .setMaxResults(1)
                .getSingleResult());
        return order;
    }

    @Override
    public List<Order> sortBy(String field, boolean flag) {
        EntityManager em = getEntityManager();
        List<Order> sortedList;
        String hql = String.format("%s %s %s", HQL_SORT_BY, field, (flag ? "ASC" : "DESC"));
        sortedList = em.createQuery(hql, Order.class).getResultList();
        return sortedList;
    }

    @Override
    public List<Order> findOrderByMechanicId(Integer mechanicId) {
        EntityManager em = getEntityManager();
        List<Order> orderList;
        orderList = em.createQuery(HQL_FIND_BY_MECHANIC, Order.class)
                .setParameter("mechanicId", mechanicId)
                .getResultList();
        return orderList;
    }

    @Override
    public List<Order> findOrderByStatus(OrderStatus orderStatus) {
        EntityManager em = getEntityManager();
        List<Order> orderList;
        orderList = em.createQuery(HQL_FIND_BY_STATUS, Order.class)
                .setParameter("status", orderStatus)
                .getResultList();
        return orderList;
    }

    @Override
    public Boolean checkIsOrderExists(Integer id) {
        EntityManager em = getEntityManager();
        if (em.find(Order.class, id) == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<Order> findOrderOverPeriodOfTime(LocalDateTime start, LocalDateTime end, String field, boolean flag) {
        EntityManager em = getEntityManager();
        List<Order> sortedOrder;
        String hql = String.format("%s %s %s", HQL_PERIOD_OF_TIME, field, (flag ? "ASC" : "DESC"));
        sortedOrder = em.createQuery(hql, Order.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        return sortedOrder;
    }
}

package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.OrderDAO;
import com.senla.task1.exceptions.JpaException;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("orderJpaDAO")
public class OrderJpaDAOImpl extends AbstractJpaDAO<Order, Integer> implements OrderDAO {

    private static final String HQL_SORT_BY = "SELECT o FROM Order o ORDER BY ";
    private static final String HQL_FIND_BY_MECHANIC = "SELECT o FROM Order o WHERE o.Mechanic.Id = :mechanicId";
    private static final String HQL_FIND_BY_STATUS = "SELECT o FROM Order o WHERE o.status = :status";
    private static final String HQL_PERIOD_OF_TIME = "SELECT o FROM Order o WHERE submissionDDateTime BETWEEN :start" +
            " AND :end ORDER BY ";
    private static final String HQL_END_DATE_TIME = "SELECT o FROM Order o " +
                    "INNER JOIN o.status s " +
                    "WHERE s.code = :waiting OR s.code = :accepted " +
                    "ORDER BY o.submissionDateTime DESC";

    private static final Logger logger = LogManager.getLogger(OrderJpaDAOImpl.class);

    public OrderJpaDAOImpl(Class<Order> type) {
        super(type);
    }

    public OrderJpaDAOImpl() {
        super(Order.class);
    }

    @Override
    public Optional<Order> getEndDateTimeLastActiveOrder() {
        Order order = null;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            order = session.createQuery(HQL_END_DATE_TIME, Order.class)
                    .setParameter("waiting", OrderStatusType.WAITING)
                    .setParameter("accepted", OrderStatusType.ACCEPTED)
                    .setMaxResults(1)
                    .uniqueResult();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при получении последнего активного заказа", e);
        } finally {
            session.close();
        }
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> sortBy(String field, boolean flag) {
        List<Order> sortedList = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            String hql = HQL_SORT_BY + field + (flag ? " ASC" : " DESC");
            sortedList = session.createQuery(hql, Order.class).getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при сортировке заказов по {}", field, e);
        } finally {
            session.close();
        }
        return sortedList;
    }

    @Override
    public List<Order> findOrderByMechanicId(Integer mechanicId) {
        List<Order> orderList = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            orderList = session.createQuery(HQL_FIND_BY_MECHANIC, Order.class)
                    .setParameter("mechanicId", mechanicId)
                    .getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении заказа по id механика {}", mechanicId, e);
        } finally {
            session.close();
        }
        return orderList;
    }

    @Override
    public List<Order> findOrderByStatus(OrderStatus orderStatus) {
        List<Order> orderList = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            orderList = session.createQuery(HQL_FIND_BY_STATUS, Order.class)
                    .setParameter("status", orderStatus)
                    .getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении заказов по статусу {}", orderStatus, e);
        } finally {
            session.close();
        }
        return orderList;
    }

    @Override
    public Boolean checkIsOrderExists(Integer id) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            if (session.get(Order.class, id) == null) {
                return false;
            }
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при проверке заказа на существование", e);
        } finally {
            session.close();
        }
        return true;
    }

    @Override
    public List<Order> findOrderOverPeriodOfTime(LocalDateTime start, LocalDateTime end, String field, boolean flag) {
        List<Order> sortedOrder = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            String hql = HQL_PERIOD_OF_TIME + field + (flag ? " ASC" : " DESC");
            sortedOrder = session.createQuery(hql, Order.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении заказов за период с {} по {} и сортировкой по полю {}", start, end, field, e);
        } finally {
            session.close();
        }
        return sortedOrder;
    }
}

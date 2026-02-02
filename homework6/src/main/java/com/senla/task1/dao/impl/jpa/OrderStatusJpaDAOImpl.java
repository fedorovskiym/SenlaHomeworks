package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.OrderStatusDAO;
import com.senla.task1.exceptions.JpaException;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderStatusJpaDAOImpl extends AbstractJpaDAO<OrderStatus, Integer> implements OrderStatusDAO {

    private static final String HQL_FIND_BY_CODE = "SELECT o from OrderStatus o WHERE code = :code";
    private static final Logger logger = LogManager.getLogger(OrderStatusJpaDAOImpl.class);

    public OrderStatusJpaDAOImpl(Class<OrderStatus> type) {
        super(type);
    }

    public OrderStatusJpaDAOImpl() {
        super(OrderStatus.class);
    }

    @Override
    public Optional<OrderStatus> findByCode(OrderStatusType code) {
        OrderStatus orderStatus = null;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            orderStatus = session.createQuery(HQL_FIND_BY_CODE, OrderStatus.class)
                    .setParameter("code", code)
                    .uniqueResult();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении статуса заказа по названию {}", code, e);
        } finally {
            session.close();
        }
        return Optional.ofNullable(orderStatus);
    }

    @Override
    public Boolean checkIsOrderStatusExists(Integer id) {
        OrderStatus orderStatus = null;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            if (session.get(OrderStatus.class, id) == null) {
                return false;
            }
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при проверке статуса на существование по id {}", id, e);
        } finally {
            session.close();
        }
        return true;
    }
}

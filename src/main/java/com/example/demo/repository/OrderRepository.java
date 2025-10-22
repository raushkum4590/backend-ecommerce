package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.orderDate DESC")
    List<Order> findByOrderStatusOrderByOrderDateDesc(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus != 'CANCELLED'")
    Double calculateTotalRevenue();
}

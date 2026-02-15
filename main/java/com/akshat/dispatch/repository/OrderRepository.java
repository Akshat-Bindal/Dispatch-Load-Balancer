package com.akshat.dispatch.repository;

import com.akshat.dispatch.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByExternalOrderId(String externalOrderId);
}
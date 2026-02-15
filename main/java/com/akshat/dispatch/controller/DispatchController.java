package com.akshat.dispatch.controller;

import com.akshat.dispatch.dto.DispatchPlanResponse;
import com.akshat.dispatch.service.DispatchPlannerService;
import com.akshat.dispatch.dto.OrderUpsertRequest;
import com.akshat.dispatch.dto.VehicleUpsertRequest;
import com.akshat.dispatch.model.OrderEntity;
import com.akshat.dispatch.model.VehicleEntity;
import com.akshat.dispatch.repository.OrderRepository;
import com.akshat.dispatch.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchPlannerService dispatchPlannerService;

    public DispatchController(OrderRepository orderRepository,
                              VehicleRepository vehicleRepository,
                              DispatchPlannerService dispatchPlannerService) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.dispatchPlannerService = dispatchPlannerService;
    }

    @PostMapping("/orders")
    public OrderEntity upsertOrder(@Valid @RequestBody OrderUpsertRequest req) {
        return orderRepository.findByExternalOrderId(req.getOrderId())
                .map(existing -> {
                    existing.setLat(req.getLat());
                    existing.setLon(req.getLon());
                    existing.setWeight(req.getWeight());
                    existing.setPriority(req.getPriority());
                    return orderRepository.save(existing);
                })
                .orElseGet(() -> {
                    OrderEntity o = new OrderEntity();
                    o.setExternalOrderId(req.getOrderId());
                    o.setLat(req.getLat());
                    o.setLon(req.getLon());
                    o.setWeight(req.getWeight());
                    o.setPriority(req.getPriority());
                    o.setCreatedAt(LocalDateTime.now());
                    return orderRepository.save(o);
                });
    }

    @PostMapping("/vehicles")
    public VehicleEntity upsertVehicle(@Valid @RequestBody VehicleUpsertRequest req) {
        return vehicleRepository.findByExternalVehicleId(req.getVehicleId())
                .map(existing -> {
                    existing.setLat(req.getLat());
                    existing.setLon(req.getLon());
                    existing.setCapacity(req.getCapacity());
                    return vehicleRepository.save(existing);
                })
                .orElseGet(() -> {
                    VehicleEntity v = new VehicleEntity();
                    v.setExternalVehicleId(req.getVehicleId());
                    v.setLat(req.getLat());
                    v.setLon(req.getLon());
                    v.setCapacity(req.getCapacity());
                    v.setCreatedAt(LocalDateTime.now());
                    return vehicleRepository.save(v);
                });
    }

    @GetMapping("/orders")
    public java.util.List<OrderEntity> listOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/vehicles")
    public java.util.List<VehicleEntity> listVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/plan")
    public DispatchPlanResponse plan() {
        return dispatchPlannerService.buildPlan();
    }
}


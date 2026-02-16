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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
    public java.util.List<OrderEntity> upsertOrders(@Valid @RequestBody JsonNode body) {

        ObjectMapper mapper = new ObjectMapper();
        java.util.List<OrderUpsertRequest> requests = new java.util.ArrayList<>();

        try {
            if (body.isArray()) {
                for (JsonNode node : body) {
                    requests.add(mapper.treeToValue(node, OrderUpsertRequest.class));
                }
            } else if (body.isObject()) {
                requests.add(mapper.treeToValue(body, OrderUpsertRequest.class));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Provide an object or array of objects.");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order payload: " + e.getMessage());
        }

        java.util.List<OrderEntity> result = new java.util.ArrayList<>();

        for (OrderUpsertRequest req : requests) {
            // manual validation is handled by @Valid on DTO usually,
            // but since we are converting manually, basic null checks are good:
            if (req.getOrderId() == null || req.getOrderId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
            }

            OrderEntity saved = orderRepository.findByExternalOrderId(req.getOrderId())
                    .map(existing -> {
                        existing.setLat(req.getLat());
                        existing.setLon(req.getLon());
                        existing.setAddress(req.getAddress());
                        existing.setWeight(req.getWeight());
                        existing.setPriority(req.getPriority());
                        return orderRepository.save(existing);
                    })
                    .orElseGet(() -> {
                        OrderEntity o = new OrderEntity();
                        o.setExternalOrderId(req.getOrderId());
                        o.setLat(req.getLat());
                        o.setLon(req.getLon());
                        o.setAddress(req.getAddress());
                        o.setWeight(req.getWeight());
                        o.setPriority(req.getPriority());
                        o.setCreatedAt(java.time.LocalDateTime.now());
                        return orderRepository.save(o);
                    });

            result.add(saved);
        }

        return result;
    }
    @PostMapping("/vehicles")
    public java.util.List<VehicleEntity> upsertVehicles(@Valid @RequestBody JsonNode body) {

        ObjectMapper mapper = new ObjectMapper();
        java.util.List<VehicleUpsertRequest> requests = new java.util.ArrayList<>();

        try {
            if (body.isArray()) {
                for (JsonNode node : body) {
                    requests.add(mapper.treeToValue(node, VehicleUpsertRequest.class));
                }
            } else if (body.isObject()) {
                requests.add(mapper.treeToValue(body, VehicleUpsertRequest.class));
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid JSON. Provide an object or array of objects."
                );
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vehicle payload: " + e.getMessage());
        }

        java.util.List<VehicleEntity> result = new java.util.ArrayList<>();

        for (VehicleUpsertRequest req : requests) {
            if (req.getVehicleId() == null || req.getVehicleId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vehicleId is required");
            }

            VehicleEntity saved = vehicleRepository.findByExternalVehicleId(req.getVehicleId())
                    .map(existing -> {
                        existing.setLat(req.getLat());
                        existing.setLon(req.getLon());
                        existing.setCapacity(req.getCapacity());
                        existing.setCurrentAddress(req.getCurrentAddress());
                        return vehicleRepository.save(existing);
                    })
                    .orElseGet(() -> {
                        VehicleEntity v = new VehicleEntity();
                        v.setExternalVehicleId(req.getVehicleId());
                        v.setLat(req.getLat());
                        v.setLon(req.getLon());
                        v.setCapacity(req.getCapacity());
                        v.setCurrentAddress(req.getCurrentAddress());
                        v.setCreatedAt(java.time.LocalDateTime.now());
                        return vehicleRepository.save(v);
                    });

            result.add(saved);
        }

        return result;
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


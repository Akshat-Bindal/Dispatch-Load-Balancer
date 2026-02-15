package com.akshat.dispatch.service;

import com.akshat.dispatch.dto.*;
import com.akshat.dispatch.model.OrderEntity;
import com.akshat.dispatch.model.Priority;
import com.akshat.dispatch.model.VehicleEntity;
import com.akshat.dispatch.repository.OrderRepository;
import com.akshat.dispatch.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DispatchPlannerService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    public DispatchPlannerService(OrderRepository orderRepository, VehicleRepository vehicleRepository) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public DispatchPlanResponse buildPlan() {
        List<OrderEntity> orders = orderRepository.findAll();
        List<VehicleEntity> vehicles = vehicleRepository.findAll();

        DispatchPlanResponse res = new DispatchPlanResponse();
        res.getSummary().setTotalOrders(orders.size());

        if (vehicles.isEmpty()) {
            for (OrderEntity o : orders) {
                res.getUnassignedOrders().add(new UnassignedOrderDto(o.getExternalOrderId(), "NO_VEHICLES_AVAILABLE"));
            }
            res.getSummary().setUnassignedOrders(orders.size());
            res.getSummary().setAssignedOrders(0);
            res.getSummary().setTotalDistanceKm(0);
            return res;
        }

        // Sort orders: priority desc, weight desc, orderId asc (deterministic)
        orders.sort((a, b) -> {
            int pr = priorityRank(b.getPriority()) - priorityRank(a.getPriority());
            if (pr != 0) return pr;

            int wt = Double.compare(b.getWeight(), a.getWeight());
            if (wt != 0) return wt;

            return a.getExternalOrderId().compareToIgnoreCase(b.getExternalOrderId());
        });

        // Track remaining capacity + assigned orders per vehicle
        Map<String, Double> remaining = new HashMap<>();
        Map<String, List<OrderEntity>> assigned = new HashMap<>();
        Map<String, VehicleEntity> vehicleById = new HashMap<>();

        for (VehicleEntity v : vehicles) {
            remaining.put(v.getExternalVehicleId(), v.getCapacity());
            assigned.put(v.getExternalVehicleId(), new ArrayList<>());
            vehicleById.put(v.getExternalVehicleId(), v);
        }

        // Greedy assignment: each order -> best vehicle (min score)
        for (OrderEntity o : orders) {
            String bestVehicleId = null;
            double bestScore = Double.POSITIVE_INFINITY;

            for (VehicleEntity v : vehicles) {
                String vid = v.getExternalVehicleId();
                double rem = remaining.get(vid);

                if (rem + 1e-9 < o.getWeight()) continue; // not enough capacity

                double d = Haversine.distanceKm(v.getLat(), v.getLon(), o.getLat(), o.getLon());

                // simple + stable: mostly distance, tiny penalty for wasting capacity
                double penalty = (rem - o.getWeight()) * 0.05; // adjust weight if you want
                double score = d + penalty;

                if (score < bestScore) {
                    bestScore = score;
                    bestVehicleId = vid;
                }
            }

            if (bestVehicleId == null) {
                res.getUnassignedOrders().add(new UnassignedOrderDto(o.getExternalOrderId(), "NO_VEHICLE_CAPACITY"));
            } else {
                assigned.get(bestVehicleId).add(o);
                remaining.put(bestVehicleId, remaining.get(bestVehicleId) - o.getWeight());
            }
        }

        // Build vehicle plans + compute routes (nearest neighbor)
        double totalDistanceAll = 0.0;
        int assignedCount = 0;

        for (VehicleEntity v : vehicles) {
            String vid = v.getExternalVehicleId();
            List<OrderEntity> vOrders = assigned.get(vid);

            if (vOrders == null || vOrders.isEmpty()) {
                VehiclePlanDto vp = new VehiclePlanDto(vid);
                vp.setTotalLoad(0);
                vp.setTotalDistanceKm(0);
                res.getVehicles().add(vp);
                continue;
            }

            List<OrderDto> route = nearestNeighborRoute(v, vOrders);
            double dist = routeDistanceKm(v.getLat(), v.getLon(), route);

            double load = vOrders.stream().mapToDouble(OrderEntity::getWeight).sum();

            VehiclePlanDto vp = new VehiclePlanDto(vid);
            vp.setTotalLoad(round2(load));
            vp.setTotalDistanceKm(round2(dist));
            vp.setRoute(route);

            res.getVehicles().add(vp);

            totalDistanceAll += dist;
            assignedCount += vOrders.size();
        }

        // Sort vehicles output by vehicleId for deterministic output
        res.setVehicles(res.getVehicles().stream()
                .sorted(Comparator.comparing(VehiclePlanDto::getVehicleId, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        res.getSummary().setAssignedOrders(assignedCount);
        res.getSummary().setUnassignedOrders(res.getUnassignedOrders().size());
        res.getSummary().setTotalDistanceKm(round2(totalDistanceAll));

        return res;
    }

    private int priorityRank(Priority p) {
        if (p == null) return 0;
        return switch (p) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private List<OrderDto> nearestNeighborRoute(VehicleEntity vehicle, List<OrderEntity> orders) {
        List<OrderEntity> remaining = new ArrayList<>(orders);
        List<OrderDto> route = new ArrayList<>();

        double currLat = vehicle.getLat();
        double currLon = vehicle.getLon();

        while (!remaining.isEmpty()) {
            int bestIdx = -1;
            double bestD = Double.POSITIVE_INFINITY;

            for (int i = 0; i < remaining.size(); i++) {
                OrderEntity o = remaining.get(i);
                double d = Haversine.distanceKm(currLat, currLon, o.getLat(), o.getLon());
                if (d < bestD) {
                    bestD = d;
                    bestIdx = i;
                }
            }

            OrderEntity chosen = remaining.remove(bestIdx);
            route.add(new OrderDto(
                    chosen.getExternalOrderId(),
                    chosen.getLat(),
                    chosen.getLon(),
                    chosen.getWeight(),
                    chosen.getPriority()
            ));

            currLat = chosen.getLat();
            currLon = chosen.getLon();
        }

        return route;
    }

    private double routeDistanceKm(double startLat, double startLon, List<OrderDto> route) {
        double total = 0.0;
        double currLat = startLat;
        double currLon = startLon;

        for (OrderDto o : route) {
            total += Haversine.distanceKm(currLat, currLon, o.getLat(), o.getLon());
            currLat = o.getLat();
            currLon = o.getLon();
        }
        return total;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}

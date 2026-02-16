package com.akshat.dispatch.service;

import com.akshat.dispatch.dto.DispatchPlanResponse;
import com.akshat.dispatch.dto.VehiclePlanDto;
import com.akshat.dispatch.model.OrderEntity;
import com.akshat.dispatch.model.Priority;
import com.akshat.dispatch.model.VehicleEntity;
import com.akshat.dispatch.repository.OrderRepository;
import com.akshat.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchPlannerServiceTest {

    @Test
    void shouldMarkAllOrdersUnassignedWhenNoVehicles() {
        OrderRepository orderRepo = mock(OrderRepository.class);
        VehicleRepository vehicleRepo = mock(VehicleRepository.class);

        when(vehicleRepo.findAll()).thenReturn(List.of());
        when(orderRepo.findAll()).thenReturn(List.of(
                order("O1", 19.0, 72.0, "Addr1", 10, Priority.HIGH),
                order("O2", 19.1, 72.1, "Addr2", 20, Priority.LOW)
        ));

        DispatchPlannerService svc = new DispatchPlannerService(orderRepo, vehicleRepo);
        DispatchPlanResponse plan = svc.buildPlan();

        assertEquals(2, plan.getSummary().getTotalOrders());
        assertEquals(0, plan.getSummary().getAssignedOrders());
        assertEquals(2, plan.getSummary().getUnassignedOrders());
        assertEquals(0.0, plan.getSummary().getTotalDistanceKm(), 1e-9);
        assertEquals(2, plan.getUnassignedOrders().size());
        assertTrue(plan.getVehicles().isEmpty());
    }

    @Test
    void shouldAssignHighPriorityFirstAndRespectCapacity() {
        OrderRepository orderRepo = mock(OrderRepository.class);
        VehicleRepository vehicleRepo = mock(VehicleRepository.class);

        VehicleEntity v1 = vehicle("V1", 19.05, 72.90, "Indiranagar", 120);
        VehicleEntity v2 = vehicle("V2", 19.10, 72.85, "Koramangala", 60);

        // total weights: 40 + 30 + 50 + 80 = 200
        // total capacity: 120 + 60 = 180 => one order must be unassigned
        OrderEntity o1 = order("O1", 19.0760, 72.8777, "MG Road", 40, Priority.HIGH);
        OrderEntity o2 = order("O2", 19.0800, 72.8800, "BTM", 30, Priority.HIGH);
        OrderEntity o3 = order("O3", 19.1200, 72.8300, "HSR", 50, Priority.MEDIUM);
        OrderEntity o4 = order("O4", 19.0200, 72.9100, "Jayanagar", 80, Priority.LOW);

        when(vehicleRepo.findAll()).thenReturn(List.of(v1, v2));
        when(orderRepo.findAll()).thenReturn(List.of(o1, o2, o3, o4));

        DispatchPlannerService svc = new DispatchPlannerService(orderRepo, vehicleRepo);
        DispatchPlanResponse plan = svc.buildPlan();

        assertEquals(4, plan.getSummary().getTotalOrders());
        assertEquals(3, plan.getSummary().getAssignedOrders());
        assertEquals(1, plan.getSummary().getUnassignedOrders());

        // Unassigned should include O4 due to capacity (80 won't fit after earlier assignment)
        assertTrue(plan.getUnassignedOrders().stream().anyMatch(u -> u.getOrderId().equals("O4")));

        // Ensure capacity constraint not violated per vehicle
        for (VehiclePlanDto vp : plan.getVehicles()) {
            double cap = vp.getCapacity();
            assertTrue(vp.getTotalLoad() <= cap + 1e-9);
        }

        // Ensure HIGH priority orders got assigned (in this scenario they should)
        boolean o1Assigned = plan.getVehicles().stream().anyMatch(v -> v.getAssignedOrders().stream().anyMatch(o -> o.getOrderId().equals("O1")));
        boolean o2Assigned = plan.getVehicles().stream().anyMatch(v -> v.getAssignedOrders().stream().anyMatch(o -> o.getOrderId().equals("O2")));
        assertTrue(o1Assigned);
        assertTrue(o2Assigned);

        // Distance should be >= 0
        assertTrue(plan.getSummary().getTotalDistanceKm() >= 0.0);
    }

    @Test
    void shouldReturnVehiclesEvenIfNoOrdersAssigned() {
        OrderRepository orderRepo = mock(OrderRepository.class);
        VehicleRepository vehicleRepo = mock(VehicleRepository.class);

        VehicleEntity v1 = vehicle("V1", 19.05, 72.90, "Indiranagar", 120);

        when(vehicleRepo.findAll()).thenReturn(List.of(v1));
        when(orderRepo.findAll()).thenReturn(List.of(
                order("O_BIG", 19.1, 72.8, "Big", 9999, Priority.HIGH) // cannot fit
        ));

        DispatchPlannerService svc = new DispatchPlannerService(orderRepo, vehicleRepo);
        DispatchPlanResponse plan = svc.buildPlan();

        assertEquals(1, plan.getVehicles().size());
        assertEquals("V1", plan.getVehicles().get(0).getVehicleId());
        assertEquals(0.0, plan.getVehicles().get(0).getTotalLoad(), 1e-9);
        assertEquals(1, plan.getUnassignedOrders().size());
        assertEquals("O_BIG", plan.getUnassignedOrders().get(0).getOrderId());
    }

    // ---------- helpers ----------
    private static OrderEntity order(String id, double lat, double lon, String addr, double weight, Priority p) {
        OrderEntity o = new OrderEntity();
        o.setExternalOrderId(id);
        o.setLat(lat);
        o.setLon(lon);
        o.setAddress(addr);
        o.setWeight(weight);
        o.setPriority(p);
        return o;
    }

    private static VehicleEntity vehicle(String id, double lat, double lon, String addr, double cap) {
        VehicleEntity v = new VehicleEntity();
        v.setExternalVehicleId(id);
        v.setLat(lat);
        v.setLon(lon);
        v.setCurrentAddress(addr);
        v.setCapacity(cap);
        return v;
    }
}
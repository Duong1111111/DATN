package com.example.DATN.controller;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;
import com.example.DATN.service.interfaces.LocationService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<LocationResponse> getAllLocations() {
        return locationService.getAll();
    }

    @GetMapping("/{id}")
    public LocationResponse getLocationById(@PathVariable Integer id) {
        return locationService.getById(id);
    }

    @PostMapping
    public LocationResponse createLocation(@RequestBody LocationRequest request) {
        return locationService.create(request);
    }
    @PutMapping("/{id}")
    public LocationResponse updateLocation(@PathVariable Integer id,@RequestBody LocationRequest request){
        return locationService.update(id, request);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<LocationResponse> activateLocation(@PathVariable Integer id) {
        LocationResponse response = locationService.activateLocation(id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{locationId}/reject")
    public ResponseEntity<BaseResponse<LocationResponse>> rejectLocation(@PathVariable Integer locationId) {
        LocationResponse response = locationService.rejectLocation(locationId);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<LocationResponse>> getPendingLocations() {
        return ResponseEntity.ok(locationService.getPendingLocations());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        locationService.delete(id);
    }
}
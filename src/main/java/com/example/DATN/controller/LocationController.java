package com.example.DATN.controller;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;
import com.example.DATN.service.interfaces.LocationService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;
    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<LocationResponse>> createLocation(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles
    ) throws IOException {

        LocationRequest request = objectMapper.readValue(data, LocationRequest.class);

        LocationResponse response = locationService.create(request, imageFiles);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
    }

    @PostMapping(value = "/staff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<LocationResponse>> createLocationbyStaff(@RequestPart("data") String data,
                                                                                @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        LocationRequest request = objectMapper.readValue(data, LocationRequest.class);
        LocationResponse response = locationService.createbyStaff(request, imageFiles);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL,response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<LocationResponse>> updateLocation(
            @PathVariable Integer id,
            @RequestPart("data") String data,
            @RequestPart(value = "images", required = false)  List<MultipartFile> imageFiles) throws IOException{

        LocationRequest request = objectMapper.readValue(data, LocationRequest.class);
        LocationResponse response = locationService.update(id, request, imageFiles);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
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
    @GetMapping("/pending/{id}")
    public ResponseEntity<BaseResponse<LocationResponse>> getPendingLocationDetail(@PathVariable Integer id) {
        LocationResponse response = locationService.getPendingLocationDetail(id);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        locationService.delete(id);
    }

    @GetMapping("/{locationId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Integer locationId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        Map<String, Object> result = new HashMap<>();
        result.put("Tổng số lượt tương tác", locationService.getTotalReach(locationId));
        result.put("Số tương tác mới", locationService.getNewInteractions(locationId, start, end));
        result.put("Tỷ lệ chuyển đổi", locationService.getConversionRate(locationId,start, end) +"%");
        result.put("So sánh với tháng trước", locationService.compareWithPreviousMonth(locationId) +"%");

        return ResponseEntity.ok(result);
    }
}
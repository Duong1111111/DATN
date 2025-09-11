package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;
import com.example.DATN.entity.Location;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {
    List<LocationResponse> getAll();
//    LocationResponse create(LocationRequest request);

    LocationResponse create(LocationRequest request, List<MultipartFile> imageFiles);

//    LocationResponse createbyStaff(LocationRequest request);

    LocationResponse createbyStaff(LocationRequest request, List<MultipartFile> imageFiles);

    LocationResponse activateLocation(Integer locationId);

    LocationResponse rejectLocation(Integer locationId);

    List<LocationResponse> getPendingLocations();

    LocationResponse getPendingLocationDetail(Integer id);

//    LocationResponse update(Integer id, LocationRequest request);

    List<LocationResponse> getLocationsByUserIdDefault();

    List<LocationResponse> getLocationsByUserIdNoAds();

    LocationResponse update(Integer id, LocationRequest request, List<MultipartFile> imageFiles);

    void delete(Integer id);

    LocationResponse getById(Integer locationId);

    long getTotalReach(Integer locationId);

    // Tương tác mới (trong khoảng thời gian)
    long getNewInteractions(Integer locationId, LocalDateTime from, LocalDateTime to);

    // Tỷ lệ chuyển đổi
    double getConversionRate(Integer locationId, LocalDateTime from, LocalDateTime to);

    double compareWithPreviousMonth(Integer locationId);
}
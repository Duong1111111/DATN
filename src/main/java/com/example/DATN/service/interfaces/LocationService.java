package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {
    List<LocationResponse> getAll();
    LocationResponse create(LocationRequest request);

    LocationResponse activateLocation(Integer locationId);

    LocationResponse update(Integer id, LocationRequest request);
    void delete(Integer id);

    LocationResponse getById(Integer locationId);
}
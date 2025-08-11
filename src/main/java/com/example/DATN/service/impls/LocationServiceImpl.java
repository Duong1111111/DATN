package com.example.DATN.service.impls;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;
import com.example.DATN.entity.Location;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.CategoryRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.service.interfaces.LocationService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private final TimeAgoUtil timeAgoUtil;

    public LocationServiceImpl(TimeAgoUtil timeAgoUtil) {
        this.timeAgoUtil = timeAgoUtil;
    }

    @Override
    public List<LocationResponse> getAll() {
        return locationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public LocationResponse create(LocationRequest request) {
        Location location = new Location();
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setLocation(request.getLocation());
        location.setPrice(request.getPrice());
        location.setOpenTime(request.getOpenTime());
        location.setCloseTime(request.getCloseTime());
        location.setImage(request.getImage());
        location.setStatus(AccountStatus.PENDING);
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location.setCategory(categoryRepository.findById(request.getCategoryId()).orElseThrow());
        location.setCreatedBy(accountRepository.findById(request.getCreatedBy()).orElseThrow());
        locationRepository.save(location);
        timeAgoUtil.notifyLocationCreated(location);
        return toResponse(location);
    }

    @Override
    public LocationResponse createbyStaff(LocationRequest request) {
        Location location = new Location();
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setLocation(request.getLocation());
        location.setPrice(request.getPrice());
        location.setOpenTime(request.getOpenTime());
        location.setCloseTime(request.getCloseTime());
        location.setImage(request.getImage());
        location.setStatus(AccountStatus.ACTIVE);
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location.setCategory(categoryRepository.findById(request.getCategoryId()).orElseThrow());
        location.setCreatedBy(accountRepository.findById(request.getCreatedBy()).orElseThrow());
        return toResponse(locationRepository.save(location));
    }

    @Override
    public LocationResponse activateLocation(Integer locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (location.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only pending locations can be activated.");
        }

        location.setStatus(AccountStatus.ACTIVE);
        location.setUpdatedAt(LocalDateTime.now());
        return toResponse(locationRepository.save(location));
    }
    @Override
    public LocationResponse rejectLocation(Integer locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (location.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only pending locations can be rejected.");
        }

        location.setStatus(AccountStatus.INACTIVE);
        location.setUpdatedAt(LocalDateTime.now());

        return toResponse(locationRepository.save(location));
    }

    @Override
    public List<LocationResponse> getPendingLocations() {
        return locationRepository.findAllByStatus(AccountStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LocationResponse update(Integer id, LocationRequest request) {
        Location location = locationRepository.findById(id).orElseThrow();
        if (request.getName() != null) location.setName(request.getName());
        if (request.getDescription() != null) location.setDescription(request.getDescription());
        if (request.getLocation() != null) location.setLocation(request.getLocation());
        if (request.getPrice() != null) location.setPrice(request.getPrice());
        if (request.getOpenTime() != null) location.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) location.setCloseTime(request.getCloseTime());
        if (request.getImage() != null) location.setImage(request.getImage());
        if (request.getStatus() != null) location.setStatus(request.getStatus());
        if (request.getCategoryId() != null) {
            location.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }
        location.setUpdatedAt(LocalDateTime.now());
        return toResponse(locationRepository.save(location));
    }

    @Override
    public void delete(Integer id) {
        locationRepository.deleteById(id);
    }
    @Override
    public LocationResponse getById(Integer locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
        return toResponse(location);
    }

    private LocationResponse toResponse(Location l) {
        LocationResponse res = new LocationResponse();
        res.setLocationId(l.getLocationId());
        res.setName(l.getName());
        res.setDescription(l.getDescription());
        res.setLocation(l.getLocation());
        res.setPrice(l.getPrice());
        res.setImage(l.getImage());
        res.setOpenTime(l.getOpenTime());
        res.setCloseTime(l.getCloseTime());
        res.setStatus(l.getStatus());
        res.setCreatedAt(l.getCreatedAt());
        res.setUpdatedAt(l.getUpdatedAt());
        res.setCategoryName(l.getCategory().getName());
        res.setCreatedByUsername(l.getCreatedBy().getUsername());
        return res;
    }
}

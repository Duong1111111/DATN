package com.example.DATN.service.impls;

import com.example.DATN.dto.request.LocationRequest;
import com.example.DATN.dto.response.LocationResponse;
import com.example.DATN.entity.*;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.*;
import com.example.DATN.service.interfaces.LocationService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.responsecode.ErrorCode;

import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private LocationViewLogRepository locationViewLogRepository;
    private final TimeAgoUtil timeAgoUtil;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public LocationServiceImpl(Storage storage, TimeAgoUtil timeAgoUtil) {
        this.timeAgoUtil = timeAgoUtil;
    }

    @Override
    public List<LocationResponse> getAll() {
        return locationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public LocationResponse create(LocationRequest request, List<MultipartFile> imageFiles) {
        Location location = new Location();
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setLocation(request.getLocation());
        location.setPrice(request.getPrice());
        location.setOpenTime(request.getOpenTime());
        location.setCloseTime(request.getCloseTime());
        location.setPhoneNumber(request.getPhoneNumber());
        location.setWebsite(request.getWebsite());
        location.setStatus(AccountStatus.PENDING);
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.isEmpty()) {
            throw new RuntimeException("Categories not found");
        }
        location.setCategories(categories);
        location.setCreatedBy(accountRepository.findById(request.getCreatedBy()).orElseThrow());
        Location savedLocation = locationRepository.save(location);
        Integer locationId = savedLocation.getLocationId();

        try {
            // Upload nhiều ảnh
            if (imageFiles != null && !imageFiles.isEmpty()) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String folderPath = "locations/" + savedLocation.getLocationId();
                        String imageUrl = imageUploadService.uploadImage(file, folderPath);

                        LocationImage locationImage = new LocationImage();
                        locationImage.setImageUrl(imageUrl);
                        locationImage.setLocation(savedLocation);

                        savedLocation.getImages().add(locationImage);
                    }
                }
                savedLocation = locationRepository.save(savedLocation);
            }

            timeAgoUtil.notifyLocationCreated(savedLocation);
            return toResponse(savedLocation);

        } catch (Exception e) {
            locationRepository.deleteById(savedLocation.getLocationId());
            throw new RuntimeException("Không thể upload ảnh, đã hủy tạo địa điểm. Lỗi: " + e.getMessage(), e);
        }
    }

    @Override
    public LocationResponse createbyStaff(LocationRequest request, List<MultipartFile> imageFiles) {
        Location location = new Location();
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setLocation(request.getLocation());
        location.setPrice(request.getPrice());
        location.setOpenTime(request.getOpenTime());
        location.setCloseTime(request.getCloseTime());
        location.setPhoneNumber(request.getPhoneNumber());
        location.setWebsite(request.getWebsite());
        location.setStatus(AccountStatus.ACTIVE);
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.isEmpty()) {
            throw new RuntimeException("Categories not found");
        }
        location.setCategories(categories);
        location.setCreatedBy(accountRepository.findById(request.getCreatedBy()).orElseThrow());
        Location savedLocation = locationRepository.save(location);
        Integer locationId = savedLocation.getLocationId();
        try {
            // Upload nhiều ảnh
            if (imageFiles != null && !imageFiles.isEmpty()) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String folderPath = "locations/" + savedLocation.getLocationId();
                        String imageUrl = imageUploadService.uploadImage(file, folderPath);

                        LocationImage locationImage = new LocationImage();
                        locationImage.setImageUrl(imageUrl);
                        locationImage.setLocation(savedLocation);

                        savedLocation.getImages().add(locationImage);
                    }
                }
                savedLocation = locationRepository.save(savedLocation);
            }

            timeAgoUtil.notifyLocationCreated(savedLocation);
            return toResponse(savedLocation);

        } catch (Exception e) {
            locationRepository.deleteById(savedLocation.getLocationId());
            throw new RuntimeException("Không thể upload ảnh, đã hủy tạo địa điểm. Lỗi: " + e.getMessage(), e);
        }
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
    public LocationResponse getPendingLocationDetail(Integer id) {
        Location location = locationRepository.findByLocationIdAndStatus(id, AccountStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));
        return toResponse(location);
    }

    @Override
    public List<LocationResponse> getLocationsByUserIdDefault() {
        Integer userId = getCurrentUserId();
        List<Location> locations = locationRepository.findLocationsByUserId(userId);

        return locations.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<LocationResponse> getLocationsByUserIdNoAds() {
        Integer userId = getCurrentUserId();
        List<Location> locations = locationRepository.findLocationsNotAdvertised(userId);

        return locations.stream()
                .map(this::toResponse)
                .toList();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }
    private Integer getCurrentUserId() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return account.getUserId();
    }


    @Override
    public LocationResponse update(Integer id, LocationRequest request, List<MultipartFile> imageFiles) {
        Location location = locationRepository.findById(id).orElseThrow();
        if (request.getName() != null) location.setName(request.getName());
        if (request.getDescription() != null) location.setDescription(request.getDescription());
        if (request.getLocation() != null) location.setLocation(request.getLocation());
        if (request.getPrice() != null) location.setPrice(request.getPrice());
        if (request.getOpenTime() != null) location.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) location.setCloseTime(request.getCloseTime());
        if (request.getPhoneNumber() != null) location.setPhoneNumber(request.getPhoneNumber());
        if (request.getWebsite() != null) location.setWebsite(request.getWebsite());
        if (request.getStatus() != null) location.setStatus(request.getStatus());
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            location.getCategories().clear();
            location.getCategories().addAll(categories);
        }
        try {
            // ✅ Xóa ảnh cũ nếu có yêu cầu
            if (request.getImagesToDelete() != null && !request.getImagesToDelete().isEmpty()) {
                List<LocationImage> imagesToRemove = location.getImages().stream()
                        .filter(img -> request.getImagesToDelete().contains(img.getId()))
                        .toList();

                location.getImages().removeAll(imagesToRemove);
            }

            // ✅ Thêm ảnh mới
            if (imageFiles != null && !imageFiles.isEmpty()) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String folderPath = "locations/" + id;
                        String imageUrl = imageUploadService.uploadImage(file, folderPath);

                        LocationImage locationImage = new LocationImage();
                        locationImage.setImageUrl(imageUrl);
                        locationImage.setLocation(location);

                        location.getImages().add(locationImage);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Không thể xử lý ảnh: " + e.getMessage(), e);
        }
        location.setUpdatedAt(LocalDateTime.now());
        Location updated = locationRepository.save(location);
        return toResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        locationRepository.deleteById(id);
    }
    @Override
    public LocationResponse getById(Integer locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
        LocationViewLog log = new LocationViewLog();
        log.setLocation(location);

        String username = getCurrentUsername();
        if (username != null) {
            Account user = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            log.setUser(user);
        }

        locationViewLogRepository.save(log);
        return toResponse(location);
    }
    @Override
    public long getTotalReach(Integer locationId) {
        long reviewCount = reviewRepository.countByLocation_LocationId(locationId);
        long favoriteCount = favoriteRepository.countByLocation_LocationId(locationId);
        return reviewCount + favoriteCount;
    }

    // Tương tác mới (trong khoảng thời gian)
    @Override
    public long getNewInteractions(Integer locationId, LocalDateTime from, LocalDateTime to) {
        long reviewCount = reviewRepository.countByLocation_LocationIdAndCreatedAtBetween(locationId, from, to);
        long favoriteCount = favoriteRepository.countByLocation_LocationIdAndCreatedAtBetween(locationId, from, to);
        return reviewCount + favoriteCount;
    }

    // Tỷ lệ chuyển đổi
    @Override
    public double getConversionRate(Integer locationId, LocalDateTime from, LocalDateTime to) {
        long totalReach = getTotalReach(locationId);
        long newInteractions = getNewInteractions(locationId, from, to);
        return totalReach == 0 ? 0 : (newInteractions * 100.0 / totalReach);
    }
    @Override
    public double compareWithPreviousMonth(Integer locationId) {
        LocalDateTime startThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startLastMonth = startThisMonth.minusMonths(1);
        LocalDateTime endLastMonth = startThisMonth.minusSeconds(1);

        long thisMonth = getNewInteractions(locationId, startThisMonth, LocalDateTime.now());
        long lastMonth = getNewInteractions(locationId, startLastMonth, endLastMonth);

        if (lastMonth == 0) return 100;
        return ((double) (thisMonth - lastMonth) / lastMonth) * 100.0;
    }

    private LocationResponse toResponse(Location l) {
        LocationResponse res = new LocationResponse();
        res.setLocationId(l.getLocationId());
        res.setName(l.getName());
        res.setDescription(l.getDescription());
        res.setLocation(l.getLocation());
        res.setPrice(l.getPrice());
        res.setImages(l.getImages().stream()
                .map(LocationImage::getImageUrl)
                .collect(Collectors.toList()));
        res.setPhoneNumber(l.getPhoneNumber());
        res.setWebsite(l.getWebsite());
        res.setOpenTime(l.getOpenTime());
        res.setCloseTime(l.getCloseTime());
        res.setStatus(l.getStatus());
        res.setCreatedAt(l.getCreatedAt());
        res.setUpdatedAt(l.getUpdatedAt());
        res.setCategoryNames(
                l.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList())
        );
        res.setCreatedByUsername(l.getCreatedBy().getUsername());
        res.setLatitude(l.getLatitude());
        res.setLongitude(l.getLongitude());
        return res;
    }
}

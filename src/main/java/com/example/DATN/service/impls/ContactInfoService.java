package com.example.DATN.service.impls;

import com.example.DATN.entity.ContactInfo;
import com.example.DATN.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoService(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    // Lấy danh sách tất cả
    public List<ContactInfo> getList() {
        return contactInfoRepository.findAll();
    }

    // Lấy chi tiết theo ID
    public Optional<ContactInfo> getDetail(Long id) {
        return contactInfoRepository.findById(id);
    }

    // Tạo mới
    public ContactInfo create(ContactInfo contactInfo) {
        return contactInfoRepository.save(contactInfo);
    }

    // Cập nhật
    public Optional<ContactInfo> update(Long id, ContactInfo contactInfo) {
        return contactInfoRepository.findById(id).map(existing -> {
            existing.setFullName(contactInfo.getFullName());
            existing.setEmail(contactInfo.getEmail());
            existing.setPhoneNumber(contactInfo.getPhoneNumber());
            existing.setNote(contactInfo.getNote());
            return contactInfoRepository.save(existing);
        });
    }

    // Xóa
    public void delete(Long id) {
        contactInfoRepository.deleteById(id);
    }
}
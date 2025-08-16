package com.example.DATN.controller;

import com.example.DATN.entity.ContactInfo;
import com.example.DATN.service.impls.ContactInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact-info")
public class ContactInfoController {

    private final ContactInfoService contactInfoService;

    public ContactInfoController(ContactInfoService contactInfoService) {
        this.contactInfoService = contactInfoService;
    }

    @GetMapping
    public ResponseEntity<List<ContactInfo>> getList() {
        return ResponseEntity.ok(contactInfoService.getList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactInfo> getDetail(@PathVariable Long id) {
        return contactInfoService.getDetail(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContactInfo> create(@RequestBody ContactInfo contactInfo) {
        return ResponseEntity.ok(contactInfoService.create(contactInfo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactInfo> update(@PathVariable Long id, @RequestBody ContactInfo contactInfo) {
        return contactInfoService.update(id, contactInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
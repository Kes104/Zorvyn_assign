package com.finance.demo.service;

import com.finance.demo.dto.FinancialRecordRequest;
import com.finance.demo.dto.FinancialRecordResponse;
import com.finance.demo.exception.BadRequestException;
import com.finance.demo.exception.ResourceNotFoundException;
import com.finance.demo.model.FinancialRecord;
import com.finance.demo.model.RecordType;
import com.finance.demo.model.Role;
import com.finance.demo.model.User;
import com.finance.demo.repository.FinancialRecordRepository;
import com.finance.demo.repository.FinancialRecordSpecifications;
import com.finance.demo.repository.UserRepository;
import com.finance.demo.security.SecurityUtils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository repository;
    private final UserRepository userRepository;

    public FinancialRecordService(FinancialRecordRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        Integer createdBy = resolveCreatedBy(request.createdBy());
        FinancialRecord record = new FinancialRecord();
        record.setCreatedBy(createdBy);
        record.setType(request.type());
        record.setCategory(request.category());
        record.setNotes(request.notes());
        record.setAmount(request.amount());
        record.setDate(request.date());
        FinancialRecord saved = repository.save(record);
        return toResponse(saved);
    }

    public FinancialRecordResponse getById(Integer id) {
        FinancialRecord record = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        return toResponse(record);
    }

    public List<FinancialRecordResponse> getAllRecords(RecordType type, String category, LocalDate startDate, LocalDate endDate, Integer createdBy) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        return repository.findAll(FinancialRecordSpecifications.withFilters(type, category, startDate, endDate, createdBy))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteRecord(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found");
        }
        repository.deleteById(id);
    }

    public FinancialRecordResponse updateRecord(Integer id, FinancialRecordRequest request) {
        FinancialRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        if (request.type() != null) {
            existing.setType(request.type());
        }
        if (request.category() != null && !request.category().isBlank()) {
            existing.setCategory(request.category());
        }
        if (request.notes() != null) {
            existing.setNotes(request.notes());
        }
        if (request.amount() != null) {
            existing.setAmount(request.amount());
        }
        if (request.date() != null) {
            existing.setDate(request.date());
        }
        if (request.createdBy() != null) {
            existing.setCreatedBy(resolveCreatedBy(request.createdBy()));
        }
        FinancialRecord saved = repository.save(existing);
        return toResponse(saved);
    }

    public List<FinancialRecordResponse> getRecentActivity(int limit) {
        return repository.findTop10ByOrderByDateDescIdDesc()
                .stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Integer resolveCreatedBy(Integer requestedCreatedBy) {
        String email = SecurityUtils.currentEmail();
        if (email == null) {
            throw new BadRequestException("Missing authenticated user");
        }
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = SecurityUtils.currentRole();

        if (requestedCreatedBy != null && role != Role.ADMIN) {
            throw new BadRequestException("Only admins can assign createdBy");
        }

        if (role == Role.ADMIN && requestedCreatedBy != null) {
            userRepository.findById(requestedCreatedBy)
                    .orElseThrow(() -> new ResourceNotFoundException("CreatedBy user not found"));
            return requestedCreatedBy;
        }

        return current.getId();
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getCreatedBy(),
                record.getType(),
                record.getCategory(),
                record.getAmount(),
                record.getDate(),
                record.getNotes()
        );
    }
}

package com.finance.demo.controller;
import com.finance.demo.dto.FinancialRecordRequest;
import com.finance.demo.dto.FinancialRecordResponse;
import com.finance.demo.model.RecordType;
import com.finance.demo.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
@SecurityRequirement(name = "bearerAuth")
public class FinancialRecordController {

    private final FinancialRecordService service;

    public FinancialRecordController(FinancialRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FinancialRecordResponse> create(@Valid @RequestBody FinancialRecordRequest record) {
        FinancialRecordResponse created = service.createRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<FinancialRecordResponse> getAll(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer createdBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return service.getAllRecords(type, category, start, end, createdBy);
    }

    @GetMapping("/{id}")
    public FinancialRecordResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}")
    public FinancialRecordResponse update(@PathVariable Integer id, @RequestBody FinancialRecordRequest record) {
        return service.updateRecord(id, record);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.deleteRecord(id);
    }
}

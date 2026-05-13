package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.ListeningMaterial;
import com.english.learn.repository.ListeningMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listening")
@RequiredArgsConstructor
public class ListeningController {

    private final ListeningMaterialRepository listeningRepo;

    @GetMapping
    public ResponseEntity<ApiResult<List<ListeningMaterial>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level) {
        List<ListeningMaterial> materials;
        if (category != null) {
            materials = listeningRepo.findByCategory(category);
        } else if (level != null) {
            materials = listeningRepo.findByDifficultyLevel(level);
        } else {
            materials = listeningRepo.findAll();
        }
        return ResponseEntity.ok(ApiResult.success(materials));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ListeningMaterial>> getById(@PathVariable Long id) {
        return listeningRepo.findById(id)
                .map(m -> ResponseEntity.ok(ApiResult.success(m)))
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.english.learn.repository;

import com.english.learn.model.ListeningMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListeningMaterialRepository extends JpaRepository<ListeningMaterial, Long> {
    List<ListeningMaterial> findByDifficultyLevel(Integer level);
    List<ListeningMaterial> findByCategory(String category);
}

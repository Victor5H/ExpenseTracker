package com.github.Victor5H.ExpenseTracker.repository;

import com.github.Victor5H.ExpenseTracker.be.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}

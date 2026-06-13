package com.github.Victor5H.ExpenseTracker.controller;

import com.github.Victor5H.ExpenseTracker.be.Tag;
import com.github.Victor5H.ExpenseTracker.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        if (tag.getTag() == null || tag.getTag().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name cannot be empty");
        }
        String trimmed = tag.getTag().trim();
        if (tagRepository.findByTag(trimmed).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists");
        }
        tag.setTag(trimmed);
        Tag saved = tagRepository.save(tag);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
    }

    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));

        if (tagDetails.getTag() == null || tagDetails.getTag().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name cannot be empty");
        }
        String trimmed = tagDetails.getTag().trim();
        tagRepository.findByTag(trimmed).ifPresent(t -> {
            if (!t.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag name already exists");
            }
        });

        tag.setTag(trimmed);
        return tagRepository.save(tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");
        }
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

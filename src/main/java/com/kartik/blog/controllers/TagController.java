package com.kartik.blog.controllers;

import com.kartik.blog.domain.dtos.CreateTagsRequest;
import com.kartik.blog.domain.dtos.TagDto;
import com.kartik.blog.domain.entities.Tag;
import com.kartik.blog.mappers.TagMapper;
import com.kartik.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path =  "/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(){
        List<Tag> tags = tagService.getTags();
        List<TagDto> tagRespons = tags.stream().map(tagMapper::toTagDto).toList();
        return ResponseEntity.ok(tagRespons);
    }

    @PostMapping
    public ResponseEntity<List<TagDto>> createTags(@RequestBody CreateTagsRequest createTagsRequest){
        List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
        List<TagDto> createTagRespons = savedTags.stream().map(tagMapper::toTagDto).toList();
        return new ResponseEntity<>(
                createTagRespons,
                HttpStatus.CREATED
        );
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id){
        tagService.deleteTag(id);
        return  ResponseEntity.noContent().build();
    }
}

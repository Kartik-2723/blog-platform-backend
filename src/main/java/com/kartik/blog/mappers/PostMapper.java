package com.kartik.blog.mappers;

import com.kartik.blog.domain.CreatePostRequest;
import com.kartik.blog.domain.UpdatePostRequest;
import com.kartik.blog.domain.dtos.CreatePostRequestDto;
import com.kartik.blog.domain.dtos.PostDto;
import com.kartik.blog.domain.dtos.UpdatePostRequestDto;
import com.kartik.blog.domain.entities.Post;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author",source = "author")
    @Mapping(target = "category",source = "category")
    @Mapping(target = "tags",source = "tags")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);

    @Mapping(target = "status", source = "status")
    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);
}

package com.kartik.blog.mappers;


import com.kartik.blog.domain.PostStatus;
import com.kartik.blog.domain.dtos.TagDto;
import com.kartik.blog.domain.entities.Post;
import com.kartik.blog.domain.entities.Tag;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "postCount" , source = "posts", qualifiedByName = "calculatePostCount")
    TagDto toTagDto(Tag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts){
        if(posts == null){
            return 0;
        }

        return  (int) posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }



}

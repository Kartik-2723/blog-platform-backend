package com.kartik.blog.services.impl;

import com.kartik.blog.domain.CreatePostRequest;
import com.kartik.blog.domain.PostStatus;
import com.kartik.blog.domain.UpdatePostRequest;
import com.kartik.blog.domain.entities.Category;
import com.kartik.blog.domain.entities.Post;
import com.kartik.blog.domain.entities.Tag;
import com.kartik.blog.domain.entities.User;
import com.kartik.blog.repositories.PostRepository;
import com.kartik.blog.services.CategoryService;
import com.kartik.blog.services.PostService;
import com.kartik.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.SecondaryTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;


    private static final int WORDS_PER_MINUTE = 200;

    @Override
    public Post getPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Post does not exist with ID "+id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {

        if(categoryId!=null && tagId!=null){
            Category categoryById = categoryService.getCategoryById(categoryId);
            Tag      tagById = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    categoryById,
                    tagById
            );
        }

        if(categoryId!=null){
            Category categoryById = categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    categoryById
            );
        }

        if(tagId!=null){
            Tag tagById = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tagById
            );
        }

        return postRepository.findAllByStatus(PostStatus.PUBLISHED);

    }

    @Override
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user,PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());

        log.info("Mapped status = {}", createPostRequest.getStatus());
        newPost.setStatus(createPostRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(calculateReadingTime(createPostRequest.getContent()));

        Category category = categoryService.getCategoryById(createPostRequest.getCategoryId());
        newPost.setCategory(category);

        createPostRequest.getTagIds();

        List<Tag> tags=tagService.getTagByIds(createPostRequest.getTagIds());
        newPost.setTags(new HashSet<>(tags));

        return postRepository.save(newPost);

    }

    @Override
    @Transactional
    public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
        Post old = postRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Post does not exist with id "+ id));

        old.setTitle(updatePostRequest.getTitle());
        old.setContent(updatePostRequest.getContent());
        old.setStatus(updatePostRequest.getStatus());
        old.setReadingTime(calculateReadingTime(updatePostRequest.getContent()));

        UUID oldPostCategoryId =  updatePostRequest.getCategoryId();
        if(!old.getCategory().getId().equals(updatePostRequest.getCategoryId())){
            old.setCategory(categoryService.getCategoryById(updatePostRequest.getCategoryId()));
        }

        Set<UUID> oldTagIds = old.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Set<UUID> updateTagIds = updatePostRequest.getTagIds();

        if(!oldTagIds.equals(updateTagIds)){
            List<Tag> newTags = tagService.getTagByIds(updateTagIds);
            old.setTags(new HashSet<>(newTags));
        }

        return postRepository.save(old);
    }

    @Override
    public void deletePost(UUID id) {
        Post post = getPost(id);
        postRepository.delete(post);
    }

    private Integer calculateReadingTime(String content){
        if(content==null || content.isEmpty()){
            return 0;
        }

        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double)wordCount / WORDS_PER_MINUTE);
    }

}

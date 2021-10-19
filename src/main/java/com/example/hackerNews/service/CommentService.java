package com.example.hackerNews.service;

import com.example.hackerNews.entity.Comment;
import com.example.hackerNews.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> findAllCommentsByNewsId(Long newsId){
        return commentRepository.findAllCommentsByNewsId(newsId);
    }


    public Comment findCommentById(Long id) {
        return commentRepository.getById(id);
    }

    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

}

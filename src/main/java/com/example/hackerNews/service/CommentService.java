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

    public List<Comment> findAllCommentsByNewsId(Long id){
        return commentRepository.findAllCommentsByNewsId(id);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment get(Long id) {
        return commentRepository.findById(id).get();
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.getById(commentId);
    }

    public void deleteCommentByNewsId(Long newsId) {
        commentRepository.deleteById(newsId);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.getCommentById(commentId);
    }

    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

}

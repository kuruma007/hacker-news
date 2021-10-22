package com.example.hackerNews.service;

import com.example.hackerNews.entity.Comment;
import com.example.hackerNews.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> findAllCommentsByNewsId(Long newsId){
        return commentRepository.findAllCommentsByNewsId(newsId);
    }

    public List<Comment> getAll(){
        return commentRepository.findAll();
    }

    public Comment findCommentById(Long id) {
        return commentRepository.getById(id);
    }

    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }
}

package com.example.hackerNews.controller;

import com.example.hackerNews.entity.Comment;
import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.service.CommentService;
import com.example.hackerNews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private NewsService newsService;

    @RequestMapping("/saveComment/{id}")
    public String saveComment(@PathVariable Long id,
                              @ModelAttribute("commentAdd") Comment comment) {
        System.out.println("hii");
        comment.setNewsId(id);
        comment.setCreatedAt(new java.util.Date().toString());
        commentService.saveComment(comment);
        return "redirect:/newsOpen/"+ id;
    }
    @RequestMapping("/newsOpen/{id}")
    public String openNews(@PathVariable Long id, Model model) {
        NewsEntity news = newsService.get(id);
        List<Comment> comments = commentService.findAllCommentsByNewsId(id);
        Comment commentAdd = new Comment();
        model.addAttribute("news", news);
        model.addAttribute("commentAdd", commentAdd);
        model.addAttribute("comments", comments);
        return "news";
    }

}

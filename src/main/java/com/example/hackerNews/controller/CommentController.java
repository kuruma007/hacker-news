package com.example.hackerNews.controller;

import com.example.hackerNews.entity.Comment;
import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.entity.User;
import com.example.hackerNews.service.CommentService;
import com.example.hackerNews.service.NewsService;
import com.example.hackerNews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsController newsController;

    @Autowired
    private UserService userService;

    @RequestMapping("/saveComment/{id}")
    public String saveComment(@PathVariable Long id,
                              @ModelAttribute("commentAdd") Comment comment) {
        comment.setNewsId(id);
        comment.setCreatedAt(new java.util.Date().toString());
        User user = userService.getCurrentUser();
        comment.setName(user.getName());
        comment.setEmail(user.getEmail());
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

    @RequestMapping("/updateComment/{commentId}")
    public ModelAndView openPostToUpdateComment(@PathVariable(value = "commentId") Long commentId, Model model) {
        ModelAndView modelAndView=new ModelAndView("updateComment");
        Long id = commentService.findCommentById(commentId).getNewsId();
        NewsEntity news = newsService.get(id);
        List<Comment> comments = commentService.findAllCommentsByNewsId(id);
        Comment comment = commentService.findCommentById(commentId);
        modelAndView.addObject("comments" , comments);
        modelAndView.addObject("commentUpdate", comment);
        modelAndView.addObject("news",news);
        return modelAndView;
    }

    @RequestMapping("/commentUpdate/{commentId}")
    public String commentUpdate(@PathVariable(value = "commentId") Long commentId,
                                @ModelAttribute("commentUpdate") Comment commentUpdate) {
        Comment comment = commentService.findCommentById(commentId);
        comment.setComment(commentUpdate.getComment());
        Long newsId = comment.getNewsId();
        commentService.saveComment(comment);
        return "redirect:/newsOpen/"+ newsId;
    }

    @PostMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable(value = "commentId") Long commentId) {
        Long id = commentService.findCommentById(commentId).getNewsId();
        commentService.deleteCommentById(commentId);
        return "redirect:/newsOpen/"+ id;
    }
}

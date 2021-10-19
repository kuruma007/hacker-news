package com.example.hackerNews.controller;

import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String showNewPostForm(Model model) {
        Page<NewsEntity> page = newsService.getPostsWithPagination(1,10,"id","asc","");
        List<NewsEntity> newsList = page.getContent();
        model.addAttribute("currentPage",1);
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("keyword","");
        model.addAttribute("newsList",newsList);
        return "showAllNews";
    }

    @GetMapping("/createNewNews")
    public String newNews(Model model) {
        NewsEntity newsEntity = new NewsEntity();
        model.addAttribute("newsEntity",newsEntity);
        return "createNews";
    }

    @PostMapping("/saveNews")
    public String saveNews(@ModelAttribute("newsEntity") NewsEntity newsEntity) {
        newsEntity.setCreatedAt(new java.util.Date().toString());
        newsEntity.setName("Ankit Srivastava");
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @PostMapping("/updateNews")
    public String updateNews(@ModelAttribute("newsEntity") NewsEntity newsEntity){
        newsEntity.setCreatedAt(new java.util.Date().toString());
        newsEntity.setName("Ankit Srivastava");
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @GetMapping("/editNews/{id}")
    public ModelAndView showEditNewsForm(@PathVariable(name = "id") Long id){
        ModelAndView modelAndView = new ModelAndView("editNews");
        NewsEntity newsEntity = newsService.get(id);
        modelAndView.addObject("newsEntity", newsEntity);
        return modelAndView;
    }

    @GetMapping("/deleteNews/{id}")
    public  String deleteNews(@PathVariable(name = "id") Long id){
        newsService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/page/{pageNo}")
    public String showPostWithPagination(@PathVariable (value="pageNo") int pageNo,
                                         @RequestParam("sortField") String sortField,
                                         @RequestParam("sortDir") String sortDir,
                                         @RequestParam("keyword") String keyword, Model model) {
        int pageSize=10;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo,pageSize,sortField,sortDir,keyword);
        List<NewsEntity> newsList = page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("newsList",newsList);
        return "showAllNews";
    }


}
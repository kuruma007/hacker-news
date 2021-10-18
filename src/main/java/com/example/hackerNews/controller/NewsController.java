package com.example.hackerNews.controller;

import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class NewsController {
    @Autowired
    private NewsService newsService;

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

    @GetMapping("/")
    public String showNews(Model model) {
        List<NewsEntity> newsList = newsService.getNews();
        model.addAttribute("newsList", newsList);
        return "showAllNews";
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

}
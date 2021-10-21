package com.example.hackerNews.controller;

import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.entity.User;
import com.example.hackerNews.service.NewsService;
import com.example.hackerNews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class NewsController {
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;

    @ModelAttribute
    public void modelAttribute(Model model){
        model.addAttribute("sessionUser", userService.findUserByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("admin",hasRole("ROLE_ADMIN"));
    }

    @GetMapping("/")
    public String showNewPostForm(Model model) {
        Page<NewsEntity> page = newsService.getPostsWithPagination(1,10,"id","asc","");
        List<NewsEntity> newsList = page.getContent();
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("currentPage",1);
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("keyword","");
        model.addAttribute("newsList",newsList);
        return "showAllNews";
    }

    @GetMapping("/login")
    public String posts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
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
        User user = userService.getCurrentUser();
        newsEntity.setName(user.getName());
        newsEntity.setLikeByUser(false);
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @PostMapping("/updateNews")
    public String updateNews(@ModelAttribute("newsEntity") NewsEntity newsEntity){
        newsEntity.setCreatedAt(new java.util.Date().toString());
        User user = userService.getCurrentUser();
        newsEntity.setName(user.getName());
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
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("newsList",newsList);
        return "showAllNews";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome.html";
    }

    @GetMapping("/pastNews/{pageNo}")
    public String displayPostWithPagination(@PathVariable (value="pageNo") int pageNo,
                                            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
                                            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                                            @RequestParam("keyword") String keyword, Model model) {
        int pageSize = 10;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo, pageSize, sortField, sortDir, keyword);
        List<NewsEntity> newsList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList", newsList);
        return "pastNews";
    }

    @GetMapping("/newNews/{pageNo}")
    public String viewPostWithPagination(@PathVariable (value="pageNo") int pageNo,
                                         @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
                                         @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                         @RequestParam("keyword") String keyword, Model model) {
        int pageSize=10;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo,pageSize,sortField,sortDir,keyword);
        List<NewsEntity> newsList = page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList",newsList);
        return "newNews";
    }

    @RequestMapping("/addLike/{newsId}")
    public String addLikeOnNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> userLikes = newsEntity.getUserLikes();
        userLikes.add(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @RequestMapping("/removeLike/{newsId}")
    public String removeLikeOnNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> userLikes = newsEntity.getUserLikes();
        userLikes.remove(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    public static boolean hasRole(String roleName) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }
}
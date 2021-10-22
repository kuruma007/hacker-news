package com.example.hackerNews.controller;

import com.example.hackerNews.entity.Comment;
import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.entity.User;
import com.example.hackerNews.service.CommentService;
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
    @Autowired
    private CommentService commentService;

    @ModelAttribute
    public void modelAttribute(Model model){
        model.addAttribute("sessionUser", userService.findUserByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()));
        model.addAttribute("admin",hasRole("ROLE_ADMIN"));
    }

    @GetMapping("/")
    public String showNewPostForm(Model model) {
        Page<NewsEntity> page = newsService.getPostsWithPagination(1,20,"id","asc","");
        List<NewsEntity> newsList = page.getContent();
        User user = userService.getCurrentUser();
        int value = 1;
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("currentPage",1);
        model.addAttribute("user", user);
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("keyword","");
        model.addAttribute("newsList",newsList);
        model.addAttribute("value", value);
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
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("newsEntity",newsEntity);

        return "createNews";
    }

    @PostMapping("/saveNews")
    public String saveNews(@ModelAttribute("newsEntity") NewsEntity newsEntity) {
        newsEntity.setCreatedAt(new java.util.Date().toString());
        User user = userService.getCurrentUser();
        newsEntity.setName(user.getName());
        newsEntity.setLikeByUser(false);
        newsEntity.setFavorite(false);
        newsEntity.setHidden(false);
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
        int pageSize=20;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo,pageSize,sortField,sortDir,keyword);
        List<NewsEntity> newsList = page.getContent();
        int value = 1;
        User user = userService.getCurrentUser();
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
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
        model.addAttribute("user", user);
        model.addAttribute("value", value);
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
        int pageSize=20;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo, pageSize, sortField, sortDir, keyword);
        List<NewsEntity> newsList = page.getContent();
        User user = userService.getCurrentUser();
        int value = 3;
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("user", user);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList", newsList);
        model.addAttribute("value", value);
        return "pastNews";
    }

    @GetMapping("/newNews/{pageNo}")
    public String viewPostWithPagination(@PathVariable (value="pageNo") int pageNo,
                                         @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
                                         @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                         @RequestParam("keyword") String keyword, Model model) {
        int pageSize=20;
        Page<NewsEntity> page = newsService.getPostsWithPagination(pageNo,pageSize,sortField,sortDir,keyword);
        List<NewsEntity> newsList = page.getContent();
        User user = userService.getCurrentUser();
        int value = 2;
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("user", user);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList",newsList);
        model.addAttribute("value", value);
        return "newNews";
    }

    @RequestMapping("/addLike/{newsId}/{value}")
    public String addLikeOnNews(@PathVariable(value = "newsId") Long newsId,
                                @PathVariable(value = "value") Integer value) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> userLikes = newsEntity.getUserLikes();
        userLikes.add(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        if (value == 1) {
            return "redirect:/";
        }
        else if(value == 2){
            return "redirect:/newNews/1?sortField=id&sortDirection=desc&keyword=";
        }
        else if(value == 3){
            return "redirect:/pastNews/1?sortField=id&sortDirection=asc&keyword=";
        }
        return "";
    }

    @RequestMapping("/removeLike/{newsId}")
    public String removeLikeOnNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> userLikes = newsEntity.getUserLikes();
        userLikes.remove(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @GetMapping("/userProfile")
    public String userProfile(Model model) {
        User user = userService.getCurrentUser();
        List<NewsEntity> newsList = newsService.getNews();
        List<Comment> commentList = commentService.getAll();
        Long newsLike = 0L;
        Long newsPosted = 0L;
        Long comments = 0L;
        for (Comment comment : commentList) {
            if(comment.getName().equalsIgnoreCase(user.getName())) {
                comments+=1;
            }
        }
        for (NewsEntity news : newsList) {
            if(news.getUserLikes().contains(userService.getCurrentUser())) {
                newsLike+=1;
            }
            if(news.getName().equalsIgnoreCase(user.getName())) {
                newsPosted+=1;
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("newsLike", newsLike);
        model.addAttribute("newsPosted", newsPosted);
        model.addAttribute("comments", comments);
        return "userProfile";
    }

    @GetMapping("/userNews")
    public String userNews(Model model) {
        List<NewsEntity> newsList = newsService.getNews();
        User user = userService.getCurrentUser();
        int value = 4;
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("newsList", newsList);
        model.addAttribute("user", user);
        model.addAttribute("value", value);
        return "userNews";
    }

    @GetMapping("/hiddenNews")
    public String hiddenNews(Model model) {
        List<NewsEntity> newsList = newsService.getNews();
        User user = userService.getCurrentUser();
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("newsList", newsList);
        return "hiddenNews";
    }

    @GetMapping("/favoriteNews")
    public String favoriteNews(Model model) {
        List<NewsEntity> newsList = newsService.getNews();
        User user = userService.getCurrentUser();
        if(userService.getCurrentUser() != null) {
            for (NewsEntity news : newsList) {
                if(news.getUserLikes().contains(userService.getCurrentUser())) {
                    news.setLikeByUser(true);
                }
                else {
                    news.setLikeByUser(false);
                }
                if(news.getHiddenNews().contains(userService.getCurrentUser())) {
                    news.setHidden(true);
                }
                else {
                    news.setHidden(false);
                }
                if(news.getFavoriteNews().contains(userService.getCurrentUser())) {
                    news.setFavorite(true);
                }
                else {
                    news.setFavorite(false);
                }
            }
        }
        for (NewsEntity news : newsList) {
            news.setPointsPerPost(news.getUserLikes().size());
        }
        model.addAttribute("newsList", newsList);
        return "favoriteNews";
    }

    @RequestMapping("/hideNews/{newsId}")
    public String hideNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> hideNews = newsEntity.getHiddenNews();
        hideNews.add(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @RequestMapping("/hideNewsFromNew/{newsId}")
    public String hideNewsFromNew(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> hideNews = newsEntity.getHiddenNews();
        hideNews.add(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/newNews";
    }

    @RequestMapping("/removeHide/{newsId}")
    public String unHideNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> hideNews = newsEntity.getHiddenNews();
        hideNews.remove(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @RequestMapping("/favoriteNews/{newsId}")
    public String favoriteNews(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> favoriteNews = newsEntity.getFavoriteNews();
        favoriteNews.add(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    @RequestMapping("/removeFavorite/{newsId}")
    public String removeFavorite(@PathVariable(value = "newsId") Long newsId) {
        NewsEntity newsEntity = newsService.get(newsId);
        List<User> favoriteNews = newsEntity.getFavoriteNews();
        favoriteNews.remove(userService.getCurrentUser());
        newsService.saveNews(newsEntity);
        return "redirect:/";
    }

    public static boolean hasRole(String roleName) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }
}
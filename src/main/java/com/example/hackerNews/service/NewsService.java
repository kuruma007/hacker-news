package com.example.hackerNews.service;

import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    public void saveNews(NewsEntity newsEntity)
    {
        this.newsRepository.save(newsEntity);
    }

    public List<NewsEntity> getNews()
    {
        return newsRepository.findAll();
    }
}

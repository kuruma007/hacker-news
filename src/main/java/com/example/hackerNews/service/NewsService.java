package com.example.hackerNews.service;

import com.example.hackerNews.entity.NewsEntity;
import com.example.hackerNews.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public NewsEntity get(Long id){
        return newsRepository.findById(id).get();
    }

    public void delete(Long id){
        newsRepository.deleteById(id);
    }

    public Page<NewsEntity> getPostsWithPagination(int pageNo, int pageSize, String sortField, String sortDir,
                                                   String keyword){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pagable = PageRequest.of(pageNo-1,pageSize,sort);
        if(keyword !="") {
            return newsRepository.findByKeyword(keyword,pagable);
        }
        else {
            return newsRepository.findAll(pagable);
        }
    }
}

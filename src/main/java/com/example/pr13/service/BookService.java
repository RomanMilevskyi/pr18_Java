package com.example.pr13.service;

import com.example.pr13.entity.Book;
import com.example.pr13.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    Page<Book> getAllBooks(String title, String author, Pageable pageable);
    void saveBook(Book book);
    void saveCategory(Category category);
    List<Book> getBooksByCategory(String categoryName);
}
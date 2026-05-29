package com.example.pr13.controller;

import com.example.pr13.entity.Book;
import com.example.pr13.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Page<Book> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return bookService.getAllBooks(title, author, pageable);
    }

    // НОВИЙ МЕТОД ДЛЯ ПЕРЕВІРКИ СТВОРЕННЯ
    @PostMapping
    public String createBook(@RequestBody Book book) {
        try {
            bookService.saveBook(book);
            return "Книгу успішно додано!";
        } catch (RuntimeException e) {
            return e.getMessage(); // Поверне текст помилки, якщо знайде дублікат
        }
    }
}
package com.example.pr13.dto;

public class BookDTO {
    private String title;
    private String author;
    private Long categoryId; // ID категорії, до якої належить книга

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
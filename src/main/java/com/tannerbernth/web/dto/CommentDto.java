package com.tannerbernth.web.dto;

public class CommentDto {

    private String name;
    private String address;
    private String message;
    private Long articleId;
    private boolean isAnon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public boolean getIsAnon() {
        return isAnon;
    }

    public void setIdAnon(boolean isAnon) {
        this.isAnon = isAnon;
    }
}
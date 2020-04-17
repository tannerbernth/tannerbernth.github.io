package com.tannerbernth.web.dto;

import javax.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ArticleDto {

    @Size(min=1, message="Enter a title")
    private String title;
    private String article;
    private boolean isPublic; 
    private MultipartFile file;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
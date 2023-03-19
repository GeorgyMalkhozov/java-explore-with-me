package ru.practicum.dto;

import javax.validation.constraints.NotBlank;


public class StatsDTO {

    private String app;
    @NotBlank(message = "поле uri не должно быть пустым")
    private String uri;
    private Long hits;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }
}

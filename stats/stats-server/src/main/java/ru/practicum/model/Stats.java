package ru.practicum.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stats {

    private String app;
    private String uri;
    private long hits;
}

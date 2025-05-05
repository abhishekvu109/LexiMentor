package com.abhi.leximentor.inventory.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class NamedObjectDTO {
    private long refId;
    private String name;
    private String genre;
    private String subGenre;
    private String description;
    private List<String> aliases;
    private String status;
    private List<String> tags;
}

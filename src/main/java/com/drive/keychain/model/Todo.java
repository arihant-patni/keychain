package com.drive.keychain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a Todo item. Matches JSON:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "delectus aut autem",
 *   "completed": false
 * }
 *
 * Uses Lombok for boilerplate (getters/setters, builders, constructors).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    private Integer userId;
    private Integer id;
    private String title;
    private Boolean completed;
}


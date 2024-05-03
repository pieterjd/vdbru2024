package be.pieterjd.vdbru2024.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Todo {
    private Integer userId;
    private Integer id;
    private String title;
    private Boolean completed;
}

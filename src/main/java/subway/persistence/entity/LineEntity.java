package subway.persistence.entity;

import java.util.Objects;

public class LineEntity {

    private final Long id;
    private final String name;
    private final String color;

    LineEntity(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineEntity(String name, String color) {
        this(null, name, color);
    }

    public static LineEntity of(Long id, LineEntity lineEntity) {
        return new LineEntity(id, lineEntity.name, lineEntity.color);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}

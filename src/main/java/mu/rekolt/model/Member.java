package mu.rekolt.model;

import java.util.Objects;

public class Member {
    private final String id;
    private final String name;

    public Member(String id, String name) {
        if (id == null || !id.matches("^M-\\d{4}$")) {
            throw new IllegalArgumentException("Invalid member id: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return id + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((Member) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
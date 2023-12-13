package de.kontext_e.aoc2023;

import java.util.Objects;

public class StringWrapper {
    private String wrapped;

    public StringWrapper(String wrapped) {
        this.wrapped = wrapped;
    }

    public int length() {
        return wrapped.length();
    }

    public char charAt(int col) {
        return wrapped.charAt(col);
    }

    public void insert(int pos, char c) {
        var before = wrapped.substring(0, pos);
        var after = wrapped.substring(pos);
        wrapped = before + c + after;
    }

    public void replace(int pos, char c) {
        if (pos >= wrapped.length()) return;
        var before = wrapped.substring(0, pos);
        var after = wrapped.substring(pos + 1);
        wrapped = before + c + after;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringWrapper that = (StringWrapper) o;
        return Objects.equals(wrapped, that.wrapped);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrapped);
    }

    @Override
    public String toString() {
        return wrapped;
    }
}

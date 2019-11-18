package dev.szczygiel.second;

public interface Buffer {
    void put(int n);

    void take(int n);

    int size();
}

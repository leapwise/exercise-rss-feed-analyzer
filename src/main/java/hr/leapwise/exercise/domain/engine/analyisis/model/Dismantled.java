package hr.leapwise.exercise.domain.engine.analyisis.model;

@FunctionalInterface
public interface Dismantled<T> {

    void add(T part);
}

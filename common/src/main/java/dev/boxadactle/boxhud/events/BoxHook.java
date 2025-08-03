package dev.boxadactle.boxhud.events;

public interface BoxHook<T> {

    void register(T listener);

    T invoker();

}

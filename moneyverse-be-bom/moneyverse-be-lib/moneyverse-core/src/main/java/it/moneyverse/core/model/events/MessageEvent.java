package it.moneyverse.core.model.events;

public interface MessageEvent<K,V> {
    K key();
    V value();
}

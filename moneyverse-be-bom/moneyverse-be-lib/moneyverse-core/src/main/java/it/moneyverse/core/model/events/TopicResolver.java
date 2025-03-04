package it.moneyverse.core.model.events;

public interface TopicResolver<T> {
  String resolveTopic(T event);
}

package it.moneyverse.test.services;

import java.util.List;

public interface ScriptService {

  <T> String createInsertScript(List<T> entities);
}

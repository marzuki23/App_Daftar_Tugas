package com.mycompany.dao;

import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author ASUS
 */
public interface GenericDAO<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void update(T entity);
}
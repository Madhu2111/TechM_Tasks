package com.ecommerce.mapper;

import com.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used for mapping between entity and model objects.
 * In this case, since we're using the entity directly, the methods simply return the input.
 */
@Component
public class ProductMapper {
    
    /**
     * Maps a product entity to a model
     * @param entity The product entity
     * @return The product model (same as entity in this implementation)
     */
    public Product entityToModel(Product entity) {
        return entity;
    }
    
    /**
     * Maps a list of product entities to models
     * @param entities The list of product entities
     * @return The list of product models (same as entities in this implementation)
     */
    public List<Product> entitiesToModels(List<Product> entities) {
        return entities;
    }
}
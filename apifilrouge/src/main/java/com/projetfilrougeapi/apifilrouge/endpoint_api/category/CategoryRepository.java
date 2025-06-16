package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);

    /**
     * Retrieves all Category entities whose 'key' property matches one of the provided keys.
     * <p>
     * It is essential for allowing the API to link entities using business-friendly identifiers
     * (like "sport", "culture") instead of technical database IDs.
     *
     * @param keys A list of strings representing the keys of the categories to find.
     * @return A list of the corresponding Category entities. If no keys match, the list will be empty.
     */
    List<Category> findByKeyIn(List<String> keys); // c'est comme faire SELECT category_id, name, description, is_trending, key FROM category WHERE key IN ('sport', 'culture');


}

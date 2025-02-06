package fr.cave.winecellar.dao;

import fr.cave.winecellar.model.Wine;
import java.util.List;
import java.util.Optional;

public interface WineDAO {
    Long create(Wine wine);
    Optional<Wine> findById(Long id);
    List<Wine> findAll();
    boolean update(Wine wine);
    boolean delete(Long id);
    List<Wine> findByYear(int year);
    Optional<Wine> findByNameAndYear(String name, int year);
}
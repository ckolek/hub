package me.kolek.hub.determinant.cache;

import me.kolek.determinant.DeterminantEvaluable;
import me.kolek.determinant.DeterminantException;
import me.kolek.hub.cache.CacheException;
import me.kolek.hub.determinant.Determinant;

import java.util.List;
import java.util.Optional;

public interface DeterminantCache {
    List<Determinant> getByType(String determinantType) throws CacheException;

    Optional<Determinant> getMatch(String determinantType, DeterminantEvaluable evaluable)
            throws CacheException, DeterminantException;

    List<Determinant> getMatches(String determinantType, DeterminantEvaluable evaluable)
            throws CacheException, DeterminantException;
}


package ru.iu3.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iu3.backend.models.Country;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository  extends JpaRepository<Country, Long>
{

    List findAll();

    Country save(Country country);

    Optional<Country> findById(Long countryId);

    void delete(Country country);
}


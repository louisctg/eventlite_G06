package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long> {

	Iterable<Venue> findAllByOrderByNameAsc();

	public Iterable<Venue> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

}

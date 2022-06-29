package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Models.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository  extends JpaRepository<Media, Long> {

    Media getByUuid(String uuid);
    List<Media> findAllByOfferId(Long offerId);
    List<Media>  findAllByOfferIdOrderBySortOrder(Long offerId);

}

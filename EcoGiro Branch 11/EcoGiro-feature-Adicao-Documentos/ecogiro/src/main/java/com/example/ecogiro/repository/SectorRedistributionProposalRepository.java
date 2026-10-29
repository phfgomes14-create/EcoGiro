package com.example.ecogiro.repository;

import com.example.ecogiro.model.SectorRedistributionProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectorRedistributionProposalRepository
        extends JpaRepository<SectorRedistributionProposal, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from SectorRedistributionProposal p where p.id = :id")
    Optional<SectorRedistributionProposal> lockById(@Param("id") Long id);

    boolean existsBySectorIdAndStatus(Long sectorId, SectorRedistributionProposal.Status status);

    List<SectorRedistributionProposal> findByStatusOrderByCreatedAtDesc(
            SectorRedistributionProposal.Status status);
}

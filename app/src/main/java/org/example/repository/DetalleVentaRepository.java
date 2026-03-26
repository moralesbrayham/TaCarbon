package org.example.repository;

import org.example.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByEstadoCocinaIn(List<DetalleVenta.EstadoCocina> estados);
}


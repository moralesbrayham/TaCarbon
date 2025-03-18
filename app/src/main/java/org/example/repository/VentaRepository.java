package org.example.repository;

import org.example.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Buscar ventas por fecha
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtener productos más vendidos
    @Query("SELECT d.producto.nombre, SUM(d.cantidad) FROM DetalleVenta d GROUP BY d.producto ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> obtenerProductosMasVendidos();

    // Obtener total de ingresos en un periodo
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin")
    Double obtenerTotalIngresos(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}


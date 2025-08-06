package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.DetalleFactura;
import com.maxiguias.maxigestion.maxigestion.modelo.Factura;
import com.maxiguias.maxigestion.maxigestion.servicio.FacturaService;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<Factura>> obtenerFacturas() {
        List<Factura> facturas = facturaService.obtenerTodasLasFacturas();
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerFacturaPorId(@PathVariable Long id) {
        Factura factura = facturaService.obtenerFacturaPorId(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleFactura>> obtenerDetallesFactura(@PathVariable Long id) {
        Factura factura = facturaService.obtenerFacturaPorId(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(factura.getDetalles());
    }

    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody Factura factura) {
        Factura facturaGuardada = facturaService.guardarFactura(factura);
        return ResponseEntity.ok(facturaGuardada);
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<String> guardarDetalleFactura(@PathVariable Long id, @RequestBody DetalleFactura detalle) {
        Factura factura = facturaService.obtenerFacturaPorId(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        detalle.setFactura(factura);
        facturaService.guardarDetalles(detalle);
        return ResponseEntity.ok("Detalle guardado exitosamente.");
    }
}
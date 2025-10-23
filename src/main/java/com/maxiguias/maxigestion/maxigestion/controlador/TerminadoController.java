package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Terminado;
import com.maxiguias.maxigestion.maxigestion.servicio.TerminadoService;

@RestController
@RequestMapping("/api/terminados")
public class TerminadoController {

    private final TerminadoService terminadoService;

    public TerminadoController(TerminadoService terminadoService) {
        this.terminadoService = terminadoService;
    }

    @GetMapping
    public ResponseEntity<List<Terminado>> obtenerTerminados() {
        List<Terminado> terminados = terminadoService.obtenerTodosLosTerminados();
        return ResponseEntity.ok(terminados);
    }

    @GetMapping("/por-producto/{id}")
    public ResponseEntity<List<Terminado>> obtenerTerminadosPorProducto(@PathVariable("id") Long productoId) {
        List<Terminado> terminados = terminadoService.obtenerTerminadosPorProducto(productoId);
        return ResponseEntity.ok(terminados);
    }
}
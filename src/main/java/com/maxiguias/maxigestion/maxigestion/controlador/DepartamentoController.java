package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Departamento;
import com.maxiguias.maxigestion.maxigestion.servicio.DepartamentoService;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> listarDepartamentos() {
        List<Departamento> departamentos = departamentoService.listarDepartamentos();
        return ResponseEntity.ok(departamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtenerDepartamentoPorId(@PathVariable Integer id) {
        Optional<Departamento> departamento = departamentoService.obtenerDepartamentoPorId(id);
        if (departamento.isPresent()) {
            return ResponseEntity.ok(departamento.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Departamento> crearDepartamento(@RequestBody Departamento departamento) {
        Departamento departamentoGuardado = departamentoService.guardarDepartamento(departamento);
        return ResponseEntity.ok(departamentoGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizarDepartamento(@PathVariable Integer id, @RequestBody Departamento departamento) {
        Optional<Departamento> departamentoExistente = departamentoService.obtenerDepartamentoPorId(id);
        if (departamentoExistente.isPresent()) {
            departamento.setId(id);
            Departamento departamentoActualizado = departamentoService.guardarDepartamento(departamento);
            return ResponseEntity.ok(departamentoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarDepartamento(@PathVariable Integer id) {
        Optional<Departamento> departamento = departamentoService.obtenerDepartamentoPorId(id);
        if (departamento.isPresent()) {
            departamentoService.eliminarDepartamento(id);
            return ResponseEntity.ok("Departamento eliminado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
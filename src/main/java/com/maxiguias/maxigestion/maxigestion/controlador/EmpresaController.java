package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Empresa;
import com.maxiguias.maxigestion.maxigestion.servicio.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> obtenerEmpresas() {
        List<Empresa> empresas = empresaService.listarEmpresas();
        return ResponseEntity.ok(empresas);
    }
}
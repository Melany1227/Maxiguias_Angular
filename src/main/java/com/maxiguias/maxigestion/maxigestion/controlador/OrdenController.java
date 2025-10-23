package com.maxiguias.maxigestion.maxigestion.controlador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.DetalleOrden;
import com.maxiguias.maxigestion.maxigestion.modelo.EstadoOrden;
import com.maxiguias.maxigestion.maxigestion.modelo.Orden;
import com.maxiguias.maxigestion.maxigestion.modelo.Producto;
import com.maxiguias.maxigestion.maxigestion.modelo.Terminado;
import com.maxiguias.maxigestion.maxigestion.modelo.Usuario;
import com.maxiguias.maxigestion.maxigestion.repositorio.CiudadRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.EmpresaRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.ProductoRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.TerminadoRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.UsuarioRepository;
import com.maxiguias.maxigestion.maxigestion.servicio.OrdenService;
import com.maxiguias.maxigestion.maxigestion.servicio.TerminadoService;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private TerminadoService terminadoService;

    @Autowired
    private TerminadoRepository terminadoRepository;

    @GetMapping("/formulario-data")
    public ResponseEntity<FormularioData> obtenerDatosFormulario() {
        FormularioData data = new FormularioData();
        data.setEmpresa(empresaRepository.findAll().get(0));
        data.setProductos(productoRepository.findAll());
        data.setCiudades(ciudadRepository.findAll());
        data.setTerminados(terminadoService.obtenerTodosLosTerminados());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/buscar-usuarios")
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<Usuario> usuarios = usuarioRepository.buscarUsuariosPorTermino(
                Arrays.asList("NATURAL", "JURIDICO"), 
                termino.trim()
            );
            
            List<Usuario> resultado = usuarios.stream()
                .limit(10)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            try {
                List<Usuario> usuarios = usuarioRepository.findByTipoUsuario_NombreIn(Arrays.asList("NATURAL", "JURIDICO"));
                String terminoBusqueda = termino.trim().toLowerCase();
                
                List<Usuario> resultado = usuarios.stream()
                    .filter(usuario -> {
                        if (usuario == null) return false;
                        
                        String nombre = usuario.getNombre() != null ? usuario.getNombre().toLowerCase() : "";
                        String primerApellido = usuario.getPrimerApellido() != null ? usuario.getPrimerApellido().toLowerCase() : "";
                        String nombreCompleto = (nombre + " " + primerApellido).trim();
                        String documento = usuario.getDocumento() != null ? usuario.getDocumento().toString() : "";
                        
                        return nombreCompleto.contains(terminoBusqueda) ||
                               nombre.contains(terminoBusqueda) ||
                               primerApellido.contains(terminoBusqueda) ||
                               documento.contains(termino.trim());
                    })
                    .limit(10)
                    .collect(Collectors.toList());
                    
                return ResponseEntity.ok(resultado);
            } catch (Exception fallbackError) {
                return ResponseEntity.ok(new ArrayList<>());
            }
        }
    }

    @PostMapping
    public ResponseEntity<String> crearOrden(@RequestBody CrearOrdenRequest request) {
        try {
            Orden ordenGuardada = ordenService.guardarOrden(request.getOrden());

            for (int i = 0; i < request.getTerminadosId().size(); i++) {
                DetalleOrden detalle = new DetalleOrden();
                Terminado terminado = terminadoRepository.findById(request.getTerminadosId().get(i))
                    .orElseThrow(() -> new RuntimeException("Terminado no encontrado"));

                detalle.setOrden(ordenGuardada);
                detalle.setTerminado(terminado);
                detalle.setCantidad(request.getCantidades().get(i));
                detalle.setValor(request.getValores().get(i));
                detalle.setDescripcion(request.getDescripciones().get(i));
                ordenService.guardarDetalles(detalle);
            }

            return ResponseEntity.ok("Orden creada satisfactoriamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear la orden: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<Orden>> listarOrdenes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filtroCliente) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaOrden").descending());
        Page<Orden> ordenesPage;
        
        if (filtroCliente != null && !filtroCliente.trim().isEmpty()) {
            try {
                Long documento = Long.parseLong(filtroCliente.trim());
                ordenesPage = ordenService.filtrarOrdenesPorClienteDocumento(documento, pageable);
            } catch (NumberFormatException e) {
                ordenesPage = ordenService.buscarOrdenesPorCliente(filtroCliente.trim(), pageable);
            }
        } else {
            ordenesPage = ordenService.obtenerOrdenesPaginadas(pageable);
        }
        
        return ResponseEntity.ok(ordenesPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDetalleResponse> obtenerOrden(@PathVariable Long id) {
        Orden orden = ordenService.obtenerOrdenPorId(id);
        if (orden == null) {
            return ResponseEntity.notFound().build();
        }

        OrdenDetalleResponse response = new OrdenDetalleResponse();
        response.setOrden(orden);
        response.setDetalles(orden.getDetalles());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarOrden(@PathVariable Long id, @RequestBody ActualizarOrdenRequest request) {
        try {
            Orden orden = ordenService.obtenerOrdenPorId(id);
            if (orden == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (orden.getEstado() != EstadoOrden.PENDIENTE && orden.getEstado() != EstadoOrden.EN_PROCESO) {
                return ResponseEntity.badRequest().body("No se puede editar esta orden en su estado actual");
            }
            
            orden.setFechaEntrega(request.getOrdenActualizada().getFechaEntrega());
            orden.setDescripcionVenta(request.getOrdenActualizada().getDescripcionVenta());
            orden.setTotalFactura(request.getOrdenActualizada().getTotalFactura());
            orden.setEstado(request.getOrdenActualizada().getEstado());
            
            Orden ordenGuardada = ordenService.guardarOrden(orden);
            
            ordenService.eliminarDetallesOrden(id);
            
            for (int i = 0; i < request.getTerminadosId().size(); i++) {
                DetalleOrden detalle = new DetalleOrden();
                Terminado terminado = terminadoRepository.findById(request.getTerminadosId().get(i))
                    .orElseThrow(() -> new RuntimeException("Terminado no encontrado"));

                detalle.setOrden(ordenGuardada);
                detalle.setTerminado(terminado);
                detalle.setCantidad(request.getCantidades().get(i));
                detalle.setValor(request.getValores().get(i));
                detalle.setDescripcion(request.getDescripciones().get(i));
                ordenService.guardarDetalles(detalle);
            }

            return ResponseEntity.ok("Orden actualizada satisfactoriamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar la orden: " + e.getMessage());
        }
    }

    // DTOs
    public static class FormularioData {
        private Object empresa;
        private List<Producto> productos;
        private List<?> ciudades;
        private List<Terminado> terminados;
        
        public Object getEmpresa() { return empresa; }
        public void setEmpresa(Object empresa) { this.empresa = empresa; }
        public List<Producto> getProductos() { return productos; }
        public void setProductos(List<Producto> productos) { this.productos = productos; }
        public List<?> getCiudades() { return ciudades; }
        public void setCiudades(List<?> ciudades) { this.ciudades = ciudades; }
        public List<Terminado> getTerminados() { return terminados; }
        public void setTerminados(List<Terminado> terminados) { this.terminados = terminados; }
    }

    public static class CrearOrdenRequest {
        private Orden orden;
        private List<Long> terminadosId;
        private List<Integer> cantidades;
        private List<BigDecimal> valores;
        private List<String> descripciones;
        
        public Orden getOrden() { return orden; }
        public void setOrden(Orden orden) { this.orden = orden; }
        public List<Long> getTerminadosId() { return terminadosId; }
        public void setTerminadosId(List<Long> terminadosId) { this.terminadosId = terminadosId; }
        public List<Integer> getCantidades() { return cantidades; }
        public void setCantidades(List<Integer> cantidades) { this.cantidades = cantidades; }
        public List<BigDecimal> getValores() { return valores; }
        public void setValores(List<BigDecimal> valores) { this.valores = valores; }
        public List<String> getDescripciones() { return descripciones; }
        public void setDescripciones(List<String> descripciones) { this.descripciones = descripciones; }
    }

    public static class ActualizarOrdenRequest {
        private Orden ordenActualizada;
        private List<Long> terminadosId;
        private List<Integer> cantidades;
        private List<BigDecimal> valores;
        private List<String> descripciones;
        
        public Orden getOrdenActualizada() { return ordenActualizada; }
        public void setOrdenActualizada(Orden ordenActualizada) { this.ordenActualizada = ordenActualizada; }
        public List<Long> getTerminadosId() { return terminadosId; }
        public void setTerminadosId(List<Long> terminadosId) { this.terminadosId = terminadosId; }
        public List<Integer> getCantidades() { return cantidades; }
        public void setCantidades(List<Integer> cantidades) { this.cantidades = cantidades; }
        public List<BigDecimal> getValores() { return valores; }
        public void setValores(List<BigDecimal> valores) { this.valores = valores; }
        public List<String> getDescripciones() { return descripciones; }
        public void setDescripciones(List<String> descripciones) { this.descripciones = descripciones; }
    }

    public static class OrdenDetalleResponse {
        private Orden orden;
        private List<DetalleOrden> detalles;
        
        public Orden getOrden() { return orden; }
        public void setOrden(Orden orden) { this.orden = orden; }
        public List<DetalleOrden> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
    }
}
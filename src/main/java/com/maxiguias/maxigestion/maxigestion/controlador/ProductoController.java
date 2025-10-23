package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maxiguias.maxigestion.maxigestion.modelo.Producto;
import com.maxiguias.maxigestion.maxigestion.servicio.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Producto> productos;

        if (keyword != null && !keyword.isEmpty()) {
            productos = productoService.buscarPorCodigoONombre(keyword);
        } else {
            productos = productoService.listarProductos();
        }

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("cantidadDisponible") Integer cantidadDisponible,
            @RequestParam(value = "imagen", required = false) String imagen) {
        
        try {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setCantidadDisponible(cantidadDisponible);
            producto.setImagen(imagen);
            
            productoService.guardarProducto(producto);
            return ResponseEntity.ok("Producto creado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarProducto(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("cantidadDisponible") Integer cantidadDisponible,
            @RequestParam(value = "imagen", required = false) String imagen) {
        
        try {
            Optional<Producto> productoExistente = productoService.obtenerProductoPorId(id);
            if (!productoExistente.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Producto producto = new Producto();
            producto.setId(id);
            producto.setNombre(nombre);
            producto.setCantidadDisponible(cantidadDisponible);
            producto.setImagen(imagen != null ? imagen : productoExistente.get().getImagen());
            
            productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok("Producto actualizado exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok("Producto eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    @GetMapping("/catalogo")
    public ResponseEntity<CatalogoResponse> obtenerCatalogo() {
        List<Producto> productos = productoService.listarProductos();
        CatalogoResponse response = new CatalogoResponse();
        response.setProductos(productos);
        return ResponseEntity.ok(response);
    }

    public static class CatalogoResponse {
        private List<Producto> productos;
        
        public List<Producto> getProductos() { return productos; }
        public void setProductos(List<Producto> productos) { this.productos = productos; }
    }
}
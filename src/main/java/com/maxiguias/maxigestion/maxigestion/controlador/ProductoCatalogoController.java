package com.maxiguias.maxigestion.maxigestion.controlador;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.dto.ProductoCatalogoDTO;
import com.maxiguias.maxigestion.maxigestion.servicio.ProductoCatalogoService;


@RestController
@RequestMapping("/api/catalogo")
public class ProductoCatalogoController {
    
    @Autowired
    private ProductoCatalogoService productoCatalogoService;
    
    @GetMapping("/productos")
    public ResponseEntity<ProductoCatalogoResponse> obtenerProductosCatalogo() {
        List<ProductoCatalogoDTO> productos = productoCatalogoService.obtenerProductosCatalogo();
        Integer totalProductos = productoCatalogoService.calcularTotalProductos();
        BigDecimal precioPromedio = productoCatalogoService.calcularPrecioPromedio();
        
        ProductoCatalogoResponse response = new ProductoCatalogoResponse();
        response.setProductos(productos);
        response.setTotalProductos(totalProductos);
        response.setPrecioPromedio(precioPromedio);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/productos/buscar")
    public ResponseEntity<ProductoCatalogoResponse> buscarProductos(@RequestBody BusquedaRequest request) {
        List<ProductoCatalogoDTO> productos;
        String busqueda = request.getBusqueda();
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoCatalogoService.buscarProductos(busqueda);
        } else {
            productos = productoCatalogoService.obtenerProductosCatalogo();
        }
        
        Integer totalProductos = productos.size();
        BigDecimal precioPromedio = BigDecimal.ZERO;
        
        if (!productos.isEmpty()) {
            BigDecimal suma = productos.stream()
                    .map(ProductoCatalogoDTO::getPrecio)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            precioPromedio = suma.divide(new BigDecimal(productos.size()), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        ProductoCatalogoResponse response = new ProductoCatalogoResponse();
        response.setProductos(productos);
        response.setTotalProductos(totalProductos);
        response.setPrecioPromedio(precioPromedio);
        
        return ResponseEntity.ok(response);
    }

    public static class ProductoCatalogoResponse {
        private List<ProductoCatalogoDTO> productos;
        private Integer totalProductos;
        private BigDecimal precioPromedio;
        
        public List<ProductoCatalogoDTO> getProductos() { return productos; }
        public void setProductos(List<ProductoCatalogoDTO> productos) { this.productos = productos; }
        public Integer getTotalProductos() { return totalProductos; }
        public void setTotalProductos(Integer totalProductos) { this.totalProductos = totalProductos; }
        public BigDecimal getPrecioPromedio() { return precioPromedio; }
        public void setPrecioPromedio(BigDecimal precioPromedio) { this.precioPromedio = precioPromedio; }
    }

    public static class BusquedaRequest {
        private String busqueda;
        
        public String getBusqueda() { return busqueda; }
        public void setBusqueda(String busqueda) { this.busqueda = busqueda; }
    }
}
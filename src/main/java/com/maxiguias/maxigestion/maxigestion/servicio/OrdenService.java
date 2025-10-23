package com.maxiguias.maxigestion.maxigestion.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxiguias.maxigestion.maxigestion.modelo.DetalleOrden;
import com.maxiguias.maxigestion.maxigestion.modelo.EstadoOrden;
import com.maxiguias.maxigestion.maxigestion.modelo.Orden;
import com.maxiguias.maxigestion.maxigestion.repositorio.DetalleOrdenRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.OrdenRepository;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Transactional
    public Orden guardarOrden(Orden orden) {
        return ordenRepository.save(orden); 
    }

    @Transactional
    public void guardarDetalles(DetalleOrden detalles) {
        detalleOrdenRepository.save(detalles); 
    }

    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepository.findAll();
    }

    public Page<Orden> obtenerOrdenesPaginadas(Pageable pageable) {
        return ordenRepository.findAll(pageable);
    }

    public Page<Orden> buscarOrdenesPorCliente(String termino, Pageable pageable) {
        return ordenRepository.findByClienteTermino(termino, pageable);
    }

    public Page<Orden> filtrarOrdenesPorClienteDocumento(Long clienteDocumento, Pageable pageable) {
        return ordenRepository.findByUsuarioDocumento(clienteDocumento, pageable);
    }
    
    public Orden obtenerOrdenPorId(Long id) {
        return ordenRepository.findById(id).orElse(null);
    }

    @Transactional
    public void actualizarEstadoOrden(Long id, EstadoOrden nuevoEstado) {
        Orden orden = obtenerOrdenPorId(id);
        if (orden != null) {
            orden.setEstado(nuevoEstado);
            ordenRepository.save(orden);
        }
    }

    @Transactional
    public void eliminarDetallesOrden(Long ordenId) {
        detalleOrdenRepository.deleteByOrdenId(ordenId);
    }

   
}
package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.implementations.ProductoBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;

import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Productos", description = "API para Gestionar Productos")
@RestController
@RequestMapping(Constants.URL_PRODUCTOS)

public class ProductoRestControllers {

    private final ProductoBusiness productoBusiness_1;
	
	@Autowired
	private IProductoBusiness productoBusiness;
	
	@Autowired
	private IStandartResponseBusiness response;

    ProductoRestControllers(ProductoBusiness productoBusiness_1) {
        this.productoBusiness_1 = productoBusiness_1;
    }
	

	@Operation(
        operationId = "listar-productos",
        summary = "Lista todos los productos",
        description = "Devuelve una lista completa de productos disponibles en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida correctamente.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontraron productos.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandartResponse.class))
        )
    })
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(){
		try {
			return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
		}catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
	

	@Operation(
        operationId = "agregar-producto",
        summary = "Agrega un nuevo producto",
        description = "Permite registrar un nuevo producto en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))
        ),
        @ApiResponse(
            responseCode = "302",
            description = "Producto ya existente.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandartResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandartResponse.class))
        )
    })
	@PostMapping(value = "")
	public ResponseEntity<?> add(@RequestBody Producto producto){
		try {
			
			Producto response = productoBusiness_1.addProducto(producto);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_PRODUCTOS + "/" + response.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		}catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (FoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
					HttpStatus.FOUND);
		}
	}
	
	
	
	
	
}

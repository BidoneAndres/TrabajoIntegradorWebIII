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
	
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(){
		try {
			return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
		}catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
	
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

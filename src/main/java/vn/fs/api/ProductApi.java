/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.entity.Category;
import vn.fs.entity.Product;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("api/products")
public class ProductApi {

	@Autowired
	ProductRepository repo;

	@Autowired
	CategoryRepository cRepo;

	@GetMapping
	public ResponseEntity<List<Product>> getAll() {
		return ResponseEntity.ok(repo.findByStatusTrue());
	}

	//lấy các sản phẩm có trạng thái (status) là true và OrderBySoldDesc dùng để sắp xếp kết quả theo số lượng bán hàng (sold) giảm dần.
	@GetMapping("bestseller")
	public ResponseEntity<List<Product>> getBestSeller() {
		return ResponseEntity.ok(repo.findByStatusTrueOrderBySoldDesc());
	}


	@GetMapping("bestseller-admin")
	public ResponseEntity<List<Product>> getBestSellerAdmin() {
		//lấy 10 sản phẩm đứng đầu theo số lượng bán hàng (sold) giảm dần.
		return ResponseEntity.ok(repo.findTop10ByOrderBySoldDesc());
	}

	@GetMapping("latest")
	public ResponseEntity<List<Product>> getLasted() {
		// để lấy các sản phẩm có trạng thái (status) là true và OrderByEnteredDateDesc dùng để sắp xếp kết quả theo ngày nhập hàng (enteredDate) giảm dần.
		//để lấy danh sách các sản phẩm mới nhất.
		return ResponseEntity.ok(repo.findByStatusTrueOrderByEnteredDateDesc());
	}

	// lấy danh sách các sản phẩm được đánh giá cao.
	@GetMapping("rated")
	public ResponseEntity<List<Product>> getRated() {
		return ResponseEntity.ok(repo.findProductRated());
	}

	//Phương thức này được sử dụng để đề xuất các sản phẩm từ cùng một danh mục nhưng không bao gồm sản phẩm đã chọn.
	@GetMapping("suggest/{categoryId}/{productId}")
	public ResponseEntity<List<Product>> suggest(@PathVariable("categoryId") Long categoryId,
			@PathVariable("productId") Long productId) {
		return ResponseEntity.ok(repo.findProductSuggest(categoryId, productId, categoryId, categoryId));
	}

	@GetMapping("category/{id}")
	public ResponseEntity<List<Product>> getByCategory(@PathVariable("id") Long id) {
		if (!cRepo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Category c = cRepo.findById(id).get();
		return ResponseEntity.ok(repo.findByCategory(c));
	}

	@GetMapping("{id}")
	public ResponseEntity<Product> getById(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	@PostMapping
	public ResponseEntity<Product> post(@RequestBody Product product) {
		if (repo.existsById(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(repo.save(product));
	}

	@PutMapping("{id}")
	public ResponseEntity<Product> put(@PathVariable("id") Long id, @RequestBody Product product) {
		if (!id.equals(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.save(product));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Product p = repo.findById(id).get();
		p.setStatus(false);
		repo.save(p);
		return ResponseEntity.ok().build();
	}

}
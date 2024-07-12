/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fs.entity.Category;
import vn.fs.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	List<Product> findByStatusTrue();

	List<Product> findByStatusTrueOrderBySoldDesc();

	List<Product> findTop10ByOrderBySoldDesc();

	List<Product> findByStatusTrueOrderByQuantityDesc();

	List<Product> findByStatusTrueOrderByEnteredDateDesc();

	List<Product> findByCategory(Category category);

	Product findByProductIdAndStatusTrue(Long id);
	
	@Query(value = "Select p.* From products p \r\n"
			+ "left join rates r on p.product_id = r.product_id\r\n"
			+ "group by p.product_id , p.name\r\n"
			+ "Order by  avg(r.rating) desc, RAND()", nativeQuery = true)
	List<Product> findProductRated();
	
	@Query(value = "(Select p.*, avg(r.rating) Rate From products p \r\n"
			+ "left join rates r on p.product_id = r.product_id\r\n"
			+ "Where (p.category_id = ?) and (p.product_id != ?)\r\n"
			+ "group by p.product_id , p.name)\r\n"
			+ "union\r\n"
			+ "(Select p.*, avg(r.rating) Rate From products p \r\n"
			+ "left join rates r on p.product_id = r.product_id\r\n"
			+ "Where p.category_id != ?\r\n"
			+ "group by p.product_id , p.name)\r\n"
			+ "Order by category_id = ? desc, Rate desc", nativeQuery = true)
	List<Product> findProductSuggest(Long id, Long id2, Long id3, Long id4);
	
}
		//id: Đây là ID của danh mục mà sản phẩm đã chọn thuộc về. Trong truy vấn, nó được sử dụng để lọc các sản phẩm từ cùng một danh mục, nhưng không bao gồm sản phẩm đã chọn.

		//id2: Đây là ID của sản phẩm đã chọn. Trong truy vấn, nó được sử dụng để loại bỏ sản phẩm đã chọn khỏi kết quả.

		//id3: Đây cũng là ID của danh mục mà sản phẩm đã chọn thuộc về. Trong truy vấn, nó được sử dụng để lọc các sản phẩm từ các danh mục khác với danh mục của sản phẩm đã chọn.

		//id4: Đây là ID của danh mục mà sản phẩm đã chọn thuộc về. Trong câu lệnh ORDER BY của truy vấn, nó được sử dụng để sắp xếp kết quả sao cho các sản phẩm thuộc cùng một danh mục với sản phẩm đã chọn được đưa lên đầu tiên.
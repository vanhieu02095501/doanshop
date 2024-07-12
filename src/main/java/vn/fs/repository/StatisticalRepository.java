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

import vn.fs.entity.Product;

@Repository
public interface StatisticalRepository extends JpaRepository<Product, Long> {

	//sum(amount): Tính tổng số tiền từ tất cả các đơn hàng trong mỗi tháng.
	//trichs xuất tháng từ này đặt hàng
	// voiws trạng thái đã đặt hàng
	// trả về mảng đối tượng val 1 = số tiền, val2= số tháng của năm đó
	@Query(value = "select sum(amount), month(order_date) from orders where year(order_date) = ? and status = 2 group by month(order_date)", nativeQuery = true)
	List<Object[]> getMonthOfYear(int year);

	//ds năm có đơn đặt hàng
	@Query(value = "select year(order_date) from orders group by year(order_date)", nativeQuery = true)
	List<Integer> getYears();

	// tính tổng doanh thu của một năm cụ thể
	@Query(value = "select sum(amount) from orders where year(order_date) = ? and status = 2", nativeQuery = true)
	Double getRevenueByYear(int year);

	//tổng số lượng sản phẩm đã bán trong mỗi danh mục.
	//(p.price*sum(p.sold)-(p.discount)*sum(p.sold)): Tính doanh thu từ các sản phẩm đã bán trong danh mục, trừ đi phần giảm giá.
	// nhóm theo danh mục và sx giảm dần giá bán
	@Query(value = "select sum(p.sold), c.category_name, (p.price*sum(p.sold)-(p.discount)*sum(p.sold)) from categories c\r\n"
			+ "join products p on p.category_id = c.category_id\r\n"
			+ "group by c.category_name order by sum(p.sold) desc", nativeQuery = true)
	List<Object[]> getCategoryBestSeller();

}

/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fs.dto.CategoryBestSeller;
import vn.fs.dto.Statistical;
import vn.fs.entity.Order;
import vn.fs.entity.Product;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.StatisticalRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/statistical")
public class  StatisticalApi {

	@Autowired
	StatisticalRepository statisticalRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ProductRepository productRepository;

	//
	@GetMapping("{year}")
	public ResponseEntity<List<Statistical>> getStatisticalYear(@PathVariable("year") int year) {
		List<Object[]> list = statisticalRepository.getMonthOfYear(year);//ds mảng object chưa doanh thu năm
		List<Statistical> listSta = new ArrayList<>();//Danh sách tạm thời chứa các đối tượng Statistical
		List<Statistical> listReal = new ArrayList<>();//Danh sách cuối cùng sẽ chứa các đối tượng Statistical cho từng tháng trong năm.
		for (int i = 0; i < list.size(); i++) {//Duyệt qua từng phần tử trong danh sách list và tạo đối tượng Statistical
												//Tháng: (int) list.get(i)[1] , Tổng số tiền: (Double) list.get(i)[0]
			Statistical sta = new Statistical((int) list.get(i)[1], null, (Double) list.get(i)[0], 0);
			listSta.add(sta);
		}
		for (int i = 1; i < 13; i++) {
			//Duyệt qua từng tháng từ 1 đến 12.
			//Tạo đối tượng Statistical mặc định cho tháng hiện tại i với tổng số tiền là 0.0.
			Statistical sta = new Statistical(i, null, 0.0, 0);
			for (int y = 0; y < listSta.size(); y++) {
				//Kiểm tra xem có đối tượng Statistical nào trong listSta tương ứng với tháng hiện tại không
				if (listSta.get(y).getMonth() == i) {
					listReal.remove(sta);// xóa default
					listReal.add(listSta.get(y));//thêm đối tượng đó vào listReal
					break;
				} else {
					listReal.remove(sta);
					listReal.add(sta);// thêm đối tượng defaute
				}
			}
		}
		return ResponseEntity.ok(listReal);
	}

	@GetMapping("/countYear")
	public ResponseEntity<List<Integer>> getYears() {
		return ResponseEntity.ok(statisticalRepository.getYears());
	}

	//lấy doanh thư một năm cụ theer
	@GetMapping("/revenue/year/{year}")
	public ResponseEntity<Double> getRevenueByYear(@PathVariable("year") int year) {
		return ResponseEntity.ok(statisticalRepository.getRevenueByYear(year));
	}

	@GetMapping("/get-all-order-success")
	public ResponseEntity<List<Order>> getAllOrderSuccess() {
		return ResponseEntity.ok(orderRepository.findByStatus(2));
	}

	//danh sách các danh mục sản phẩm bán chạy nhất cùng với số lượng sản phẩm đã bán và tổng doanh thu từ những sản phẩm
	@GetMapping("/get-category-seller")
	public ResponseEntity<List<CategoryBestSeller>> getCategoryBestSeller() {
		List<Object[]> list = statisticalRepository.getCategoryBestSeller();//lấy danh sách các danh mục bán chạy nhất dưới dạng danh sách các mảng đối tượng
		List<CategoryBestSeller> listCategoryBestSeller = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			CategoryBestSeller categoryBestSeller = new CategoryBestSeller(String.valueOf(list.get(i)[1]),// tên danh mục (category name)
					Integer.valueOf(String.valueOf(list.get(i)[0])),//lượng sản phẩm đã bán (total quantity sold)
					Double.valueOf(String.valueOf(list.get(i)[2])));//tổng doanh thu (total revenue)
			listCategoryBestSeller.add(categoryBestSeller);
		}
		return ResponseEntity.ok(listCategoryBestSeller);
	}


	// danh sách các sản phẩm còn hàng trong kho
	@GetMapping("/get-inventory")
	public ResponseEntity<List<Product>> getInventory() {
		return ResponseEntity.ok(productRepository.findByStatusTrueOrderByQuantityDesc());
	}

}

package com.app.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.model.Product;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
	interface myView {
		//copy from getter methods
		String getVendorCode();
		String getProdName();
		Double getProdCost();
	}
	Optional<Product> findByVendorCode(String vendorCode);
    List<myView> findByVendorCodeLike(String vendorCode); // Partial search
    boolean existsByVendorCode(String vendorCode);
    void deleteByVendorCode(String vendorCode);

}

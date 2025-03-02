package com.app.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.app.model.Product;
import com.app.repo.ProductRepository;
import com.app.repo.ProductRepository.myView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyRunner.class);

    @Autowired
    private ProductRepository repo;

    @Override
    public void run(String... args) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            LOGGER.info("Enter your choice:\n1.Insert 2.Update 3.Delete 4.Display 5.Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    insert(sc, repo);
                    break;
                case 2:
                    update(sc, repo);
                    break;
                case 3:
                    delete(sc, repo);
                    break;
                case 4:
                    display(sc, repo);
                    break;
                case 5:
                	System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
System.out.println("Invalid choice! Please enter a valid option.");
            }
        }
    }

    private static void insert(Scanner sc, ProductRepository repo) {
    	System.out.println("Enter product id:");
        Integer prodId = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter vendor code:");
        String vendorCode = sc.nextLine();
    	System.out.println("Enter product name:");
        String name = sc.nextLine();

        System.out.println("Enter product price:");
        double price = sc.nextDouble();
        sc.nextLine();


        Product product = new Product(prodId, vendorCode, name, price);
        repo.save(product);
        System.out.println("Product inserted successfully.");
    }

    private static void update(Scanner sc, ProductRepository repo) {
    	System.out.println("Enter vendor code to update:");
        String code = sc.nextLine();

        Optional<Product> existingProduct = repo.findByVendorCode(code);
        if (existingProduct.isPresent()) {
        	System.out.println("Enter new product name:");
            String name = sc.nextLine();

            System.out.println("Enter new product price:");
            double price = sc.nextDouble();
            sc.nextLine();

            Product updatedProduct = existingProduct.get();
            updatedProduct.setProdName(name);
            updatedProduct.setProdCost(price);

            repo.save(updatedProduct);
            System.out.println("Product updated successfully.");
        } else {
        	System.out.println("Product with vendor code '" + code + "' not found.");
        }
    }

    private static void delete(Scanner sc, ProductRepository repo) {
    	System.out.println("Enter product code to search:");
        String code = sc.nextLine();


        if (repo.existsByVendorCode(code)) {
            repo.deleteByVendorCode(code);
            System.out.println("Product deleted successfully.");
        } else {
        	System.out.println("Product with vendor code '" + code + "' not found.");
        }
    }

    private static void display(Scanner sc, ProductRepository repo) {
    	System.out.println("Enter product code to search:");
        String code = sc.nextLine();

        List<myView> products = repo.findByVendorCodeLike(code);
        if (!products.isEmpty()) {
            for (myView p : products) {
            	System.out.println("ID: " + p.getVendorCode() + ", Name: " + p.getProdName() + ", Price: " + p.getProdCost());
            }
        } else {
        	System.out.println("No products found");
        }
    }
}

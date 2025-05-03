import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-admin-product-form',
  templateUrl: './admin-product-form.component.html',
  styleUrls: ['./admin-product-form.component.css']
})
export class AdminProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productId: number | null = null;
  submitted = false;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private notificationService: NotificationService
  ) {
    this.productForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0.01)]],
      category: ['', Validators.required],
      brand: [''],
      imageUrl: [''],
      stockQuantity: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id'] && params['id'] !== 'new') {
        this.isEditMode = true;
        this.productId = +params['id'];
        this.loadProduct(this.productId);
      }
    });
  }

  // Getter for easy access to form fields
  get f() { return this.productForm.controls; }

  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        // Populate the form with product data
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          category: product.category,
          brand: product.brand,
          imageUrl: product.imageUrl,
          stockQuantity: product.stockQuantity
        });
      },
      error: (error) => {
        console.error('Error loading product', error);
        this.notificationService.showError('Failed to load product details');
        this.router.navigate(['/admin/products']);
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.productForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const productData: Product = this.productForm.value;

    if (this.isEditMode && this.productId) {
      // Update existing product
      this.productService.updateProduct(this.productId, productData).subscribe({
        next: () => {
          this.notificationService.showSuccess('Product updated successfully');
          this.router.navigate(['/admin/products']);
        },
        error: (error) => {
          console.error('Error updating product', error);
          this.notificationService.showError('Failed to update product');
          this.isSubmitting = false;
        }
      });
    } else {
      // Create new product
      this.productService.createProduct(productData).subscribe({
        next: () => {
          this.notificationService.showSuccess('Product created successfully');
          this.router.navigate(['/admin/products']);
        },
        error: (error) => {
          console.error('Error creating product', error);
          this.notificationService.showError('Failed to create product');
          this.isSubmitting = false;
        }
      });
    }
  }
}

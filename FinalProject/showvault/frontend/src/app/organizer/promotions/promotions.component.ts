import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { CommonModule, DecimalPipe } from '@angular/common';
import { Promotion } from '../../models/show-interfaces.model';

// Using the shared Promotion interface from show-interfaces.model.ts

@Component({
  selector: 'app-promotions',
  templateUrl: './promotions.component.html',
  styleUrls: ['./promotions.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, DecimalPipe]
})
export class PromotionsComponent implements OnInit {
  promotions: Promotion[] = [];
  showId: number | null = null;
  isLoading = true;
  error = '';
  successMessage = '';
  
  // For creating/editing promotions
  promotionForm: FormGroup;
  isEditing = false;
  selectedPromotionId: number | null = null;
  showPromotionModal = false;
  
  // For filtering
  statusFilter: 'all' | 'ACTIVE' | 'INACTIVE' | 'EXPIRED' = 'all';
  searchQuery = '';

  constructor(
    private showService: ShowService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.promotionForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern('^[A-Z0-9]{4,10}$')]],
      description: ['', [Validators.required]],
      discountType: ['PERCENTAGE', [Validators.required]],
      discountValue: [10, [Validators.required, Validators.min(0), Validators.max(100)]],
      startDate: [new Date(), [Validators.required]],
      endDate: [this.getDefaultEndDate(), [Validators.required]],
      maxUses: [100, [Validators.required, Validators.min(1)]],
      minPurchaseAmount: [null, [Validators.min(0)]],
      status: ['ACTIVE', [Validators.required]]
    }, { validator: this.dateRangeValidator });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.showId = +params['id'];
        this.loadPromotions(this.showId);
      } else {
        this.loadAllPromotions();
      }
    });
  }

  getDefaultEndDate(): Date {
    const date = new Date();
    date.setMonth(date.getMonth() + 1);
    return date;
  }

  dateRangeValidator(form: FormGroup) {
    const startDate = form.get('startDate')?.value;
    const endDate = form.get('endDate')?.value;
    
    if (startDate && endDate && new Date(startDate) >= new Date(endDate)) {
      form.get('endDate')?.setErrors({ dateRange: true });
      return { dateRange: true };
    }
    
    return null;
  }

  loadPromotions(showId: number): void {
    this.isLoading = true;
    this.showService.getPromotions(showId).subscribe({
      next: (promotions) => {
        this.promotions = promotions;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = `Failed to load promotions for show #${showId}. Please try again.`;
        this.isLoading = false;
        console.error('Error loading promotions:', error);
      }
    });
  }

  loadAllPromotions(): void {
    this.isLoading = true;
    this.showService.getAllPromotions().subscribe({
      next: (promotions) => {
        this.promotions = promotions;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load promotions. Please try again.';
        this.isLoading = false;
        console.error('Error loading all promotions:', error);
      }
    });
  }

  openCreateModal(): void {
    this.isEditing = false;
    this.selectedPromotionId = null;
    this.promotionForm.reset({
      name: '',
      code: this.generatePromoCode(),
      discountType: 'percentage',
      discountValue: 10,
      startDate: new Date(),
      endDate: this.getDefaultEndDate(),
      maxUses: 100,
      status: 'active',
      description: ''
    });
    this.showPromotionModal = true;
  }

  openEditModal(promotion: Promotion): void {
    this.isEditing = true;
    this.selectedPromotionId = promotion.id || null;
    this.promotionForm.patchValue({
      name: promotion.name,
      code: promotion.code,
      discountType: promotion.discountType,
      discountValue: promotion.discountValue,
      startDate: new Date(promotion.startDate),
      endDate: new Date(promotion.endDate),
      maxUses: promotion.maxUses,
      status: promotion.status,
      description: promotion.description || ''
    });
    this.showPromotionModal = true;
  }

  closePromotionModal(): void {
    this.showPromotionModal = false;
  }

  savePromotion(): void {
    if (this.promotionForm.invalid || !this.showId) return;
    
    const promotionData: Promotion = {
      ...this.promotionForm.value,
      showId: this.showId
    };
    
    if (this.isEditing && this.selectedPromotionId) {
      // Update existing promotion
      this.showService.updatePromotion(this.showId, this.selectedPromotionId, promotionData).subscribe({
        next: (updatedPromotion) => {
          // Update the promotion in the local array
          const index = this.promotions.findIndex(p => p.id === this.selectedPromotionId);
          if (index !== -1) {
            this.promotions[index] = updatedPromotion;
          }
          
          this.successMessage = 'Promotion updated successfully.';
          this.closePromotionModal();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to update promotion. Please try again.';
          console.error('Error updating promotion:', error);
        }
      });
    } else {
      // Create new promotion
      this.showService.createPromotion(this.showId, promotionData).subscribe({
        next: (newPromotion) => {
          this.promotions.push(newPromotion);
          this.successMessage = 'Promotion created successfully.';
          this.closePromotionModal();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to create promotion. Please try again.';
          console.error('Error creating promotion:', error);
        }
      });
    }
  }

  deletePromotion(promotionId: number): void {
    if (!this.showId) return;
    
    if (confirm('Are you sure you want to delete this promotion? This action cannot be undone.')) {
      this.showService.deletePromotion(this.showId, promotionId).subscribe({
        next: () => {
          this.promotions = this.promotions.filter(p => p.id !== promotionId);
          this.successMessage = 'Promotion deleted successfully.';
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to delete promotion. Please try again.';
          console.error('Error deleting promotion:', error);
        }
      });
    }
  }

  generatePromoCode(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < 6; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }

  regenerateCode(): void {
    this.promotionForm.patchValue({
      code: this.generatePromoCode()
    });
  }

  get filteredPromotions(): Promotion[] {
    let filtered = [...this.promotions];
    
    // Apply status filter
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(promotion => promotion.status === this.statusFilter);
    }
    
    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(promotion => 
        promotion.name.toLowerCase().includes(query) ||
        promotion.code.toLowerCase().includes(query) ||
        (promotion.description && promotion.description.toLowerCase().includes(query))
      );
    }
    
    return filtered;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'active':
        return 'badge bg-success';
      case 'inactive':
        return 'badge bg-secondary';
      case 'expired':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  getDiscountDisplay(promotion: Promotion): string {
    if (promotion.discountType === 'PERCENTAGE') {
      return `${promotion.discountValue}%`;
    } else {
      return `$${promotion.discountValue.toFixed(2)}`;
    }
  }

  getFormattedDate(dateString: string | Date): string {
    if (!dateString) return 'N/A';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getUsagePercentage(promotion: Promotion): number {
    if (!promotion.currentUses || !promotion.maxUses) return 0;
    return (promotion.currentUses / promotion.maxUses) * 100;
  }

  getUsageClass(promotion: Promotion): string {
    const percentage = this.getUsagePercentage(promotion);
    if (percentage < 50) {
      return 'bg-success';
    } else if (percentage < 80) {
      return 'bg-warning';
    } else {
      return 'bg-danger';
    }
  }
}
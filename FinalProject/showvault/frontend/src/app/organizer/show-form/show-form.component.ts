import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Show } from '../../models/show.model';

@Component({
  selector: 'app-show-form',
  templateUrl: './show-form.component.html',
  styleUrls: ['./show-form.component.css']
})
export class ShowFormComponent implements OnInit {
  @Input() show: Show | null = null;
  @Output() save = new EventEmitter<Show>();
  @Output() cancel = new EventEmitter<void>();

  showForm: FormGroup;
  showTypes = ['Movie', 'Theater', 'Concert'];

  constructor(private fb: FormBuilder) {
    this.showForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      type: ['', Validators.required],
      image: ['', Validators.required],
      date: ['', Validators.required],
      time: ['', Validators.required],
      venue: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      totalSeats: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    if (this.show) {
      this.showForm.patchValue(this.show);
    }
  }

  onSubmit(): void {
    if (this.showForm.valid) {
      const formValue = this.showForm.value;
      const showData: Show = {
        ...formValue,
        id: this.show?.id || 0,
        availableSeats: this.show?.availableSeats || formValue.totalSeats
      };
      this.save.emit(showData);
    } else {
      Object.keys(this.showForm.controls).forEach(key => {
        const control = this.showForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  // Helper methods for form validation
  isFieldInvalid(fieldName: string): boolean {
    const field = this.showForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.showForm.get(fieldName);
    if (!control) return '';

    if (control.hasError('required')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    if (control.hasError('minlength')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least ${control.errors?.['minlength'].requiredLength} characters`;
    }
    if (control.hasError('min')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be greater than ${control.errors?.['min'].min}`;
    }
    return '';
  }
}
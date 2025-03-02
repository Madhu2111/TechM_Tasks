import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-stuform',
  templateUrl: './stuform.component.html',
  styleUrls: ['./stuform.component.css']
})
export class StuformComponent implements OnInit {
  registrationForm: FormGroup;
  submitted = false;
  formData: any = {};
  constructor(private fb: FormBuilder) {
    this.registrationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      age: ['', [Validators.required, Validators.min(18)]],
      password: ['', [Validators.required, Validators.minLength(6), this.requiresNumber]]
    });
   }
   
  

  ngOnInit(): void {
  }
  requiresNumber(control: AbstractControl): ValidationErrors | null {
    return /\d/.test(control.value) ? null : { requiresNumber: true };
  }
  registerStudent() {
    if (this.registrationForm.valid) {
      this.formData = this.registrationForm.value;
      this.submitted = true;
    }
  }
}



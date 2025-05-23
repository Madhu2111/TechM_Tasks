import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProductFormComponent } from './admin-product-form.component';

describe('AdminProductFormComponent', () => {
  let component: AdminProductFormComponent;
  let fixture: ComponentFixture<AdminProductFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminProductFormComponent]
    });
    fixture = TestBed.createComponent(AdminProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

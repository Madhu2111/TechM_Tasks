import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StuformComponent } from './stuform.component';

describe('StuformComponent', () => {
  let component: StuformComponent;
  let fixture: ComponentFixture<StuformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StuformComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StuformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

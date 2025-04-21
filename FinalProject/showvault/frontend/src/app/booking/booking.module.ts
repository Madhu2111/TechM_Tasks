import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { SeatSelectionComponent } from './seat-selection/seat-selection.component';
import { PaymentFormComponent } from './payment-form/payment-form.component';
import { BookingConfirmationComponent } from './booking-confirmation/booking-confirmation.component';
import { BookingSummaryComponent } from './booking-summary/booking-summary.component';
import { BookingTestComponent } from './booking-test/booking-test.component';
import { TheaterSelectionComponent } from './theater-selection/theater-selection.component';
import { AuthGuard } from '../guards/auth.guard';
import { SafeHtmlPipe } from '../pipes/safe-html.pipe';

const routes: Routes = [
  {
    path: 'theater-selection/:id',
    component: TheaterSelectionComponent
  },
  {
    path: 'theaters/:id',  // Keep for backward compatibility
    component: TheaterSelectionComponent
  },
  { 
    path: 'seat-selection', 
    component: SeatSelectionComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'payment', 
    component: PaymentFormComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'confirmation', 
    component: BookingConfirmationComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'test', 
    component: BookingTestComponent 
  }
];

@NgModule({
  declarations: [
    SeatSelectionComponent,
    PaymentFormComponent,
    BookingConfirmationComponent,
    BookingSummaryComponent,
    BookingTestComponent,
    TheaterSelectionComponent,
    SafeHtmlPipe
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ],
  exports: [
    SeatSelectionComponent,
    PaymentFormComponent,
    BookingConfirmationComponent,
    BookingSummaryComponent,
    TheaterSelectionComponent
  ]
})
export class BookingModule { }
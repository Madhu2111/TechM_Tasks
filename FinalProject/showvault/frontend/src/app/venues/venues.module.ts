import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { VenueListComponent } from './venue-list/venue-list.component';

@NgModule({
  declarations: [
    VenueListComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    VenueListComponent
  ]
})
export class VenuesModule { }
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { AdminSidebarComponent } from './admin-sidebar/admin-sidebar.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { SystemHealthComponent } from './system-health/system-health.component';
import { AdminReportsComponent } from './admin-reports/admin-reports.component';
import { PlatformSettingsComponent } from './platform-settings/platform-settings.component';
import { AuditLogsComponent } from './audit-logs/audit-logs.component';
import { BookingManagementComponent } from './booking-management/booking-management.component';

const routes: Routes = [
  { 
    path: '', 
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'users', component: UserManagementComponent },
      { path: 'bookings', component: BookingManagementComponent },
      { path: 'system-health', component: SystemHealthComponent },
      { path: 'reports', component: AdminReportsComponent },
      { path: 'settings', component: PlatformSettingsComponent },
      { path: 'audit-logs', component: AuditLogsComponent }
    ]
  }
];

@NgModule({
  declarations: [
    AdminLayoutComponent,
    AdminSidebarComponent,
    AdminDashboardComponent,
    UserManagementComponent,
    BookingManagementComponent,
    SystemHealthComponent,
    AdminReportsComponent,
    PlatformSettingsComponent,
    AuditLogsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ],
  exports: [
    AdminLayoutComponent,
    AdminSidebarComponent,
    AdminDashboardComponent,
    UserManagementComponent,
    BookingManagementComponent,
    SystemHealthComponent,
    AdminReportsComponent,
    PlatformSettingsComponent,
    AuditLogsComponent
  ]
})
export class AdminModule { }
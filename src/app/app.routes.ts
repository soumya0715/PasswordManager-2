import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { VaultComponent } from './features/vault/vault.component';
import {SecurityAuditComponent } from './features/security/security-audit.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'vault', component: VaultComponent },
  { path: 'audit', component: SecurityAuditComponent }, 
  { path: 'profile', redirectTo: 'dashboard' }
];
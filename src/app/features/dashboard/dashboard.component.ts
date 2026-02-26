import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../core/services/dashboard.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: any = null;
  // Login ke response se 'firstName' localStorage mein save kar lena chahiye
  userName: string = localStorage.getItem('user_firstName') || 'User';

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dashboardService.getSummary().subscribe({
      next: (res) => {
        this.stats = res.data;
      },
      error: (err) => {
        console.error("Failed to load dashboard data", err);
        // Agar token expire ho gaya ho toh login par bhejein
        if(err.status === 401) {
          this.onLogout();
        }
      }
    });
  }

  onLogout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
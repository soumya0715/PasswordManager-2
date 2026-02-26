import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder, 
    private auth: AuthService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(7)]]
    });
  }

  onLogin() {
  if (this.loginForm.valid) {
    this.loading = true;
    this.auth.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        // Validation check: res aur res.data dono hone chahiye
        if (res && res.data) {
          this.auth.saveToken(res); 
          this.router.navigate(['/dashboard']);
        } else {
          alert("Login data mismatch!");
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
        console.error("Login failed", err);
        alert(err.error?.message || "Invalid credentials");
      }
    });
  }
}
  }

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router'; // RouterModule add karein
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule], 
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(7)]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]], 
      securityQuestions: this.fb.array([]) 
    });
  }

  get questions() {
    return this.registerForm.get('securityQuestions') as FormArray;
  }

  addQuestion() {
    const qGroup = this.fb.group({
      question: ['', Validators.required],
      answer: ['', Validators.required]
    });
    this.questions.push(qGroup);
  }

  removeQuestion(index: number) {
    this.questions.removeAt(index);
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const qCount = this.questions.length;
      if (qCount > 0 && qCount < 3) {
        alert('Please add at least 3 security questions or remove all.');
        return;
      }

      this.loading = true;
      // Exact Postman logic
      const payload = {
        ...this.registerForm.value,
        securityQuestions: qCount >= 3 ? this.registerForm.value.securityQuestions : []
      };

      this.auth.register(payload).subscribe({
        next: (res) => {
          alert('Account Created Successfully!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          alert(err.error?.message || 'Registration failed');
          this.loading = false;
        }
      });
    }
  }
}
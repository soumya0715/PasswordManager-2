import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SecurityService } from '../../core/services/security.service';

@Component({
  selector: 'app-security-audit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './security-audit.component.html',
  styleUrls: ['./security-audit.component.css']
})
export class SecurityAuditComponent implements OnInit {
  questionsExist: boolean = false;
  questionsCount: number = 0;
  showModal: boolean = false;
  isEditMode: boolean = false;
  masterPassword: string = '';
  userName: string = localStorage.getItem('user_firstName') || 'User';
  userId: number = Number(localStorage.getItem('user_id')) || 1; // User ID backup ke liye 1 rakha hai

  savedQuestionsWithIds: any[] = [];

  // Dropdown ke liye questions
  predefinedQuestions = [
    "What is your pet name?",
    "What is your favourite color?",
    "What was your favorite subject in school?",
    "Which city were you born in?",
    "What was the name of your first school?"
  ];

  selectedQuestions = [
    { question: '', answer: '' },
    { question: '', answer: '' },
    { question: '', answer: '' }
  ];

  constructor(private securityService: SecurityService) {}

  ngOnInit() {
    this.checkAuditStatus();
  }

  checkAuditStatus() {
    this.securityService.getViewQuestions().subscribe({
      next: (res: any) => {
        const list = res?.data || [];
        this.questionsCount = list.length;
        this.questionsExist = this.questionsCount > 0;
        this.savedQuestionsWithIds = list;
      },
      error: (err: any) => console.error("Audit load failed", err)
    });
  }

  startSetup() {
    this.isEditMode = false;
    this.resetForm();
    this.showModal = true;
  }

  openEditModal() {
    this.isEditMode = true;
    this.resetForm();
    // Edit mode mein existing data pre-fill karne ki koshish
    if (this.savedQuestionsWithIds.length >= 3) {
      for (let i = 0; i < 3; i++) {
        this.selectedQuestions[i].question = this.savedQuestionsWithIds[i].question;
      }
    }
    this.showModal = true;
  }

  resetForm() {
    this.selectedQuestions = [
      { question: '', answer: '' },
      { question: '', answer: '' },
      { question: '', answer: '' }
    ];
    this.masterPassword = '';
  }

  isFormValid(): boolean {
    const questionsFilled = this.selectedQuestions.every(q => q.question !== '' && q.answer.trim() !== '');
    return this.isEditMode ? (questionsFilled && this.masterPassword.trim().length > 0) : questionsFilled;
  }

  handleSave() {
    // Postman Payload structure mapping
    const formattedPayload = this.selectedQuestions.map(q => ({
      question: q.question,
      answer: q.answer.trim(),
      user_id: this.userId
    }));

    if (this.isEditMode) {
      const updatePayload = {
        password: this.masterPassword,
        questions: formattedPayload
      };
      
      this.securityService.updateQuestions(updatePayload).subscribe({
        next: () => this.onSuccess("Security questions updated successfully!"),
        error: (err: any) => this.handleError(err, "Update")
      });
    } else {
      // POSTMAN MATCHED: Direct array of objects
      this.securityService.addQuestions(formattedPayload).subscribe({
        next: () => this.onSuccess("Recovery questions added successfully!"),
        error: (err: any) => this.handleError(err, "Add")
      });
    }
  }

  onSuccess(msg: string) {
    alert(msg);
    this.showModal = false;
    setTimeout(() => this.checkAuditStatus(), 800);
  }

  handleError(err: any, action: string) {
    console.error(`${action} Error:`, err);
    if (err.status === 403) {
      alert("Session Expired or Unauthorized! 403 Forbidden.");
    } else {
      alert(`${action} Failed: ` + (err.error?.message || "Check network connection"));
    }
  }

  onLogout() {
    localStorage.clear();
    window.location.href = '/login';
  }
}
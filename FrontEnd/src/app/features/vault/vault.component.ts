import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VaultService } from '../../core/services/vault.service';

@Component({
  selector: 'app-vault',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './vault.component.html',
  styleUrls: ['./vault.component.css']
})
export class VaultComponent implements OnInit {
  allPasswords: any[] = [];
  passwords: any[] = [];
  
  categories = [
    { id: 1, name: 'Social Accounts' },
    { id: 2, name: 'Banking Accounts' },
    { id: 3, name: 'Email Accounts' },
    { id: 4, name: 'Shopping Accounts' },
    { id: 5, name: 'Work Accounts' },
    { id: 6, name: 'Other Accounts' }
  ];

  userName: string = localStorage.getItem('user_firstName') || 'User';
  searchKeyword: string = '';
  selectedCategory: string = 'All';
  sortBy: string = 'newest';
  showFavoritesOnly: boolean = false;
  
  // UI States
  showAddModal: boolean = false;
  showMasterPassModal: boolean = false;
  showToast: boolean = false;
  toastMsg: string = '';
  isEditMode: boolean = false;
  currentEditId: number | null = null;
  
  masterPasswordInput: string = '';
  selectedItemForView: any = null;

  newPassword = {
    title: '', username: '', password: '',
    email: '', url: '', notes: '', categoryId: 1
  };

  constructor(private vaultService: VaultService, private router: Router) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData() {
    this.vaultService.getAllPasswords().subscribe({
      next: (res) => {
        this.allPasswords = res;
        this.applyAllFiltersAndSort();
      },
      error: (err) => this.triggerToast("Failed to load data! ❌")
    });
  }

  // --- Notification System ---
  triggerToast(msg: string) {
    this.toastMsg = msg;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  // --- Modal Logic ---
  openAddModal() {
    this.isEditMode = false;
    this.resetForm();
    this.showAddModal = true;
  }

  closeModal() {
    this.showAddModal = false;
    this.resetForm();
  }

  resetForm() {
    this.newPassword = {
      title: '', username: '', password: '',
      email: '', url: '', notes: '', categoryId: 1
    };
    this.currentEditId = null;
  }

  // --- CRUD Operations ---
  savePassword() {
    if (!this.newPassword.title || !this.newPassword.password) {
      this.triggerToast("Title & Password are required! ⚠️");
      return;
    }

    const request = (this.isEditMode && this.currentEditId) 
      ? this.vaultService.updatePassword(this.currentEditId, this.newPassword)
      : this.vaultService.createPassword(this.newPassword);

    request.subscribe({
      next: () => {
        this.triggerToast(this.isEditMode ? "Updated! 🔄" : "Saved! 🛡️");
        this.closeModal();
        this.loadInitialData();
      },
      error: () => this.triggerToast("Something went wrong! ❌")
    });
  }

  editPassword(p: any) {
    this.isEditMode = true;
    this.currentEditId = p.id;
    this.newPassword = { ...p };
    this.showAddModal = true;
  }

 // Naye variables
showDeleteModal: boolean = false;
idToDelete: number | null = null;

// Delete button par click hone par
deletePassword(id: number) {
  this.idToDelete = id;
  this.showDeleteModal = true; // Browser confirm ki jagah modal khulega
}

// Modal mein 'Delete' confirm karne par
confirmDelete() {
  if (this.idToDelete) {
    this.vaultService.deletePassword(this.idToDelete).subscribe({
      next: () => {
        this.allPasswords = this.allPasswords.filter(p => p.id !== this.idToDelete);
        this.applyAllFiltersAndSort();
        this.triggerToast("Deleted! 🗑️");
        this.closeDeleteModal();
      },
      error: () => this.triggerToast("Failed to delete! ❌")
    });
  }
}

closeDeleteModal() {
  this.showDeleteModal = false;
  this.idToDelete = null;
}

  // --- Unlock Logic ---
  openViewModal(p: any) {
    this.selectedItemForView = p;
    this.masterPasswordInput = '';
    this.showMasterPassModal = true;
  }

  confirmMasterPassword() {
    if (!this.masterPasswordInput) return;
    
    this.vaultService.viewPassword(this.selectedItemForView.id, this.masterPasswordInput).subscribe({
      next: (res: any) => {
        const pass = res.data?.password || res.password;
        this.showMasterPassModal = false;
        this.copyToClipboard(pass, "Password Decrypted & Copied! ✅");
      },
      error: () => this.triggerToast("Wrong Master Password! 🔐")
    });
  }

  copyToClipboard(text: string, customMsg?: string) {
    if (!text) return;
    navigator.clipboard.writeText(text).then(() => {
      this.triggerToast(customMsg || "Copied to clipboard! 📋");
    });
  }

  autoGenerate() {
    this.vaultService.generatePassword({ length: 14, includeNumbers: true, includeSpecialChars: true }).subscribe({
      next: (res: any) => {
        this.newPassword.password = res.data?.passwords[0] || res[0] || '';
      }
    });
  }

  // --- Filtering ---
  applyAllFiltersAndSort() {
    let temp = [...this.allPasswords];
    if (this.selectedCategory !== 'All') {
      temp = temp.filter(p => p.categoryName === this.selectedCategory);
    }
    if (this.searchKeyword.trim()) {
      const kw = this.searchKeyword.toLowerCase();
      temp = temp.filter(p => p.title.toLowerCase().includes(kw) || (p.username && p.username.toLowerCase().includes(kw)));
    }
    if (this.showFavoritesOnly) {
      temp = temp.filter(p => p.isFavorite === true);
    }
    temp.sort((a, b) => {
      if (this.sortBy === 'name') return a.title.localeCompare(b.title);
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });
    this.passwords = temp;
  }

  onFilterChange() { this.applyAllFiltersAndSort(); }

  toggleFavorite(id: number) {
    this.vaultService.toggleFavorite(id).subscribe(() => {
      const item = this.allPasswords.find(p => p.id === id);
      if (item) item.isFavorite = !item.isFavorite;
      this.applyAllFiltersAndSort();
    });
  }

  exportVault() {
    this.vaultService.exportVault().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `RevVault_Backup.enc`;
        a.click();
        this.triggerToast("Exporting Encrypted File... 📤");
      }
    });
  }

  onLogout() { 
    localStorage.clear(); 
    this.router.navigate(['/login']); 
  }
}
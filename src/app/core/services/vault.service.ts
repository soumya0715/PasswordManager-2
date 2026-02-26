import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VaultService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ==========================================
  // 1. PASSWORD CONTROLLER (@RequestMapping("/passwords"))
  // ==========================================
  
  getAllPasswords(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/passwords`);
  }

  getFavoritePasswords(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/passwords/favorites`);
  }

 
  viewPassword(id: number, masterPassword: string): Observable<any> {
    return this.http.post(`${this.api}/passwords/${id}/view`, { password: masterPassword });
  }

  createPassword(passwordDto: any): Observable<any> {
    return this.http.post(`${this.api}/passwords`, passwordDto);
  }

  updatePassword(id: number, passwordDto: any): Observable<any> {
    return this.http.put(`${this.api}/passwords/${id}`, passwordDto);
  }

  deletePassword(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/passwords/${id}`);
  }

  toggleFavorite(id: number): Observable<any> {
    return this.http.post(`${this.api}/passwords/${id}/favourite`, {});
  }

  searchPasswords(keyword: string): Observable<any[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<any[]>(`${this.api}/passwords/search`, { params });
  }

  // ==========================================
  // 2. CATEGORY CONTROLLER (@RequestMapping("/categories"))
  // ==========================================

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/categories/GetCategory`);
  }

  createCategory(name: string, description: string): Observable<any> {
    return this.http.post(`${this.api}/categories/CreateCategory`, { name, description });
  }

  // ==========================================
  // 3. BACKUP CONTROLLER (@RequestMapping("/backup"))
  // ==========================================

  exportVault(): Observable<Blob> {
    return this.http.get(`${this.api}/backup/export`, { responseType: 'blob' });
  }

  importVault(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.api}/backup/import`, formData);
  }

  // ==========================================
  // 4. GENERATOR CONTROLLER (@RequestMapping("/Password-generator"))
  // ==========================================


  generatePassword(config: any): Observable<any> {
    return this.http.post(`${this.api}/Password-generator/generate`, config);
  }


  generateAndSave(config: any): Observable<any> {
    return this.http.post(`${this.api}/Password-generator/generate-and-save`, config);
  }
}
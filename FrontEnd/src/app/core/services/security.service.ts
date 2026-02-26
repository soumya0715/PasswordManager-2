import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SecurityService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
  
    const token = localStorage.getItem('auth_token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getAuditReport(): Observable<any> {
    return this.http.get<any>(`${this.api}/security/audit`, { headers: this.getAuthHeaders() });
  }

  getViewQuestions(): Observable<any> {
    return this.http.get<any>(`${this.api}/security-questions/View`, { headers: this.getAuthHeaders() });
  }

  addQuestions(questions: any[]): Observable<any> {

    return this.http.post<any>(`${this.api}/security-questions/Add`, questions, { headers: this.getAuthHeaders() });
  }

  updateQuestions(payload: any): Observable<any> {
    return this.http.post<any>(`${this.api}/security-questions/Update`, payload, { headers: this.getAuthHeaders() });
  }
}
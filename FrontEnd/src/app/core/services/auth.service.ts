import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment'; // Import environment
import { RegisterRequest } from '../models/user.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  
  private baseUrl = `${environment.apiUrl}/auth`; 

  constructor(private http: HttpClient) {}

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, credentials);
  }

saveToken(response: any) {
 
  console.log("Backend Response:", response);

  if (response && response.data) {
    localStorage.setItem('auth_token', response.data.token);
    localStorage.setItem('user_firstName', response.data.firstName);
  
    localStorage.setItem('user_id', response.data.userId.toString());
  } else {
    console.error("Token missing in backend response!", response);
  }
}
  }

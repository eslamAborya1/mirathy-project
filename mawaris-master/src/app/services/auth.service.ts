import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {catchError, Observable, tap, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isLoggedInSignal = signal(false);
  isLoggedIn$ = this.isLoggedInSignal.asReadonly();


  private BASE_URL = 'http://localhost:8087/api/v1/auth';

  constructor(private http: HttpClient) {
    this.checkStoredLogin();
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.BASE_URL}/login`, { email, password })
      .pipe(
        tap(res => {
          localStorage.setItem('token', res.accessToken);
          this.isLoggedInSignal.set(true);
        }),
        catchError(err => {
          // Forward a clean error message to the component
          const message =
            err?.error?.message || 'Invalid email or password';

          return throwError(() => message);
        })
      );
  }

  register(fullName: string, email: string, password: string): Observable<any> {
    return this.http.post(`${this.BASE_URL}/register`, { fullName, email, password });
  }

  logout(): void {
    localStorage.removeItem('token');
    this.isLoggedInSignal.set(false);
  }

  private checkStoredLogin(): void {
    const token = localStorage.getItem('token');
    if (token) this.isLoggedInSignal.set(true);
  }

  getIsLoggedIn(): boolean {
    return this.isLoggedInSignal();
  }
}
// login-response.interface.ts
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  fullName: string;
  email: string;
  role: 'USER' | 'ADMIN'; // adjust if you have more roles
  isActive: boolean;
  createdAt: string; // or Date if you convert it
  message: string;
  success: boolean;
}



import {ChangeDetectorRef, Component, inject, OnChanges, SimpleChanges} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login implements OnChanges{
  private cdr = inject(ChangeDetectorRef);

  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      Object.values(this.loginForm.controls).forEach(c => c.markAsTouched());
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { email, password } = this.loginForm.value;

    console.log(this.loginForm.value);

    this.authService.login(email, password).subscribe({
      next: () => {
        this.router.navigate(['/home']);
        this.isLoading = false;
      },
      error: (message: string) => {
        this.errorMessage = message;
        this.isLoading = false;
        this.cdr.detectChanges();
        console.log(message);
      },
      complete: () => {
        this.isLoading = false;
      }
    });

  }
  ngOnChanges() {
    this.onSubmit();
  }
}

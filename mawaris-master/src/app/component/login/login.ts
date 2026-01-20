import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

function noSpacesValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  return /\s/.test(value) ? { spaces: true } : null;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor(private toastr: ToastrService) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, noSpacesValidator]],
      password: ['', [Validators.required, Validators.minLength(6), noSpacesValidator]]
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

    this.authService.login(email, password).subscribe({
      next: () => {
        this.router.navigate(['/home']);
        console.log(localStorage.getItem("token"))
        this.isLoading = false;
        console.log('Login successful');
        this.toastr.success('تم تسجيل الدخول بنجاح!', 'مرحباً بك', {
          positionClass: 'toast-bottom-left',
          timeOut: 7000
        });
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message || 'البريد الإلكتروني أو كلمة المرور غير صحيحة';
        this.isLoading = false;
      }
    });
  }
}

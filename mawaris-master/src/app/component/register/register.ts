
import {ChangeDetectorRef, Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';

function noSpacesValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  return /\s/.test(value) ? { spaces: true } : null;
}

function validNameValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  // منع الأرقام والعلامات والنقاط في البديه
  if (!/^[a-zA-Z\u0600-\u06FF\s]/.test(value)) {
    return { invalidName: true };
  }
  return null;
}

function passwordStartValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  // Password must start with a character (a-z, A-Z) or number (0-9)
  return /^[a-zA-Z0-9]/.test(value) ? null : { passwordStart: true };
}

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  if (!password || !confirmPassword) return null;
  return password.value === confirmPassword.value ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {

  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  private cdr = inject(ChangeDetectorRef);

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    this.registerForm = this.fb.group(
      {
        fullName: ['', [Validators.required, Validators.minLength(3), noSpacesValidator, validNameValidator]],
        email: ['', [Validators.required, Validators.email, noSpacesValidator]],
        password: ['', [Validators.required, Validators.minLength(6), noSpacesValidator, passwordStartValidator]],
        confirmPassword: ['', [Validators.required, noSpacesValidator]]
      },
      { validators: passwordMatchValidator }
    );
  }

  get f() {
    return this.registerForm.controls;
  }

  register() {
    if (this.registerForm.invalid) {
      Object.values(this.registerForm.controls).forEach(c => c.markAsTouched());
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { fullName, email, password } = this.registerForm.value;

    this.authService.register(fullName, email, password).subscribe({
      next: () => {
        this.successMessage = 'تم إنشاء الحساب بنجاح';
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message || 'حدث خطأ أثناء التسجيل';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}


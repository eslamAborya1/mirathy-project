// import { Component, inject } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterModule } from '@angular/router';
// import { AuthService } from '../../services/auth.service';

// @Component({
//   selector: 'app-navbar',
//   standalone: true,
//   imports: [CommonModule, RouterModule],
//   templateUrl: './navbar.html',
//   styleUrl: './navbar.css'
// })
// export class Navbar {
//   authService = inject(AuthService);

//   logout() {
//     this.authService.logout();
//   }
// }

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar {
  constructor(private router: Router, private toastr: ToastrService) {}
  authService = inject(AuthService);
  menuOpen = false;

  logout() {
    const isConfirmed = confirm('هل أنت متأكد من رغبتك في تسجيل الخروج؟');
    if (isConfirmed) {
      this.authService.logout();
      this.toastr.success('تم تسجيل الخروج بنجاح', 'نجح', {
        positionClass: 'toast-bottom-left',
        timeOut: 2000
      });
      this.menuOpen = false;
      setTimeout(() => {
        window.location.reload();
      }, 800);
    }
  }
}

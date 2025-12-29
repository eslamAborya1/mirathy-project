import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './not-found-page.html',
  styleUrls: ['./not-found-page.css'],
})
export class NotFoundPage {
  isLoading = false;

  constructor(private router: Router) {}

  goHome() {
    this.isLoading = true; 
    setTimeout(() => {
      this.router.navigate(['/home']);
      this.isLoading = false;
    }, 1500); 
  }
}

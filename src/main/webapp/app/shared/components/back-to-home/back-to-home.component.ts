import { Component, inject } from '@angular/core';
import { Router, provideRouter } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-back-to-home',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './back-to-home.component.html',
  styleUrls: ['./back-to-home.component.scss'],
})
export class BackToHomeComponent {
  private router = inject(Router);

  goHome(): void {
    // Navigate to root/home
    this.router.navigate(['/']);
  }
}

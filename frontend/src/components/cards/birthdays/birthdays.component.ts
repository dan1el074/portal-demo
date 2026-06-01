import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { AvatarComponent, CardBodyComponent, CardComponent, CardTitleDirective } from '@coreui/angular';
import { Birthday } from '../../../app/interface/birthday.interface';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-birthdays',
  imports: [
    CommonModule,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    AvatarComponent
  ],
  templateUrl: './birthdays.component.html',
  styleUrl: './birthdays.component.scss',
})
export class BirthdaysComponent implements OnInit {
  @Input() todayBirthdays!: Array<Birthday>;
  @Input() monthBirthdays!: Array<Birthday>;
  protected apiUrl = environment.apiUrl;
  protected currentMonth!: string;

  public ngOnInit(): void {
    this.currentMonth = new Date().toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }).toUpperCase();
  }
}

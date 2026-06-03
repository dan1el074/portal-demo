import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ButtonModule, CardModule } from '@coreui/angular';
import { HomeCardStat } from './../../../app/interface/user.interface';
import { RouterLink } from '@angular/router';
import { NotificationWebSocketService } from '../../../app/services/websocket.service';

@Component({
  selector: 'app-hello',
  imports: [
    CommonModule,
    CardModule,
    ButtonModule,
    RouterLink
  ],
  templateUrl: './hello.component.html',
  styleUrl: './hello.component.scss',
})
export class HelloComponent {
  @Input() userName!: string;
  @Input() userEmail!: string;
  @Input() fileCount!: number;
  @Input() upcomingEvents!: number;
  @Input() openOrders!: number;
  @Input() openMemorandos!: number;

  readonly stats: HomeCardStat[] = [];

  constructor(protected websocket: NotificationWebSocketService) {}

  public ngOnInit(): void {
    this.stats.push(
      {
        label: 'ARQUIVOS',
        title: 'Arquivos',
        value: this.fileCount,
        iconPath:
          'M8.25 6.75h7.5M8.25 9.75h7.5M8.25 12.75h4.5m-7.5-9H18a2.25 2.25 0 012.25 2.25v13.5A2.25 2.25 0 0118 21.75H6a2.25 2.25 0 01-2.25-2.25V6a2.25 2.25 0 012.25-2.25z',
        iconColor: '#6366f1',
        iconBg: 'rgba(99,102,241,0.12)',
      },
      {
        label: 'EVENTOS',
        title: 'Eventos',
        value: this.upcomingEvents,
        iconPath:
          'M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5',
        iconColor: '#f59e0b',
        iconBg: 'rgba(245,158,11,0.12)',
      },
      {
        label: 'Notificações',
        title: 'Novas notificações',
        value: 0,
        iconPath:
          'M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0',
        iconColor: '#22c55e',
        iconBg: 'rgba(34,197,94,0.12)',
      },
      {
        label: 'MEMORANDOS',
        title: 'Memorandos abertos',
        value: this.openMemorandos,
        iconPath:
          'M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75',
        iconColor: '#3b82f6',
        iconBg: 'rgba(59,130,246,0.12)',
      }
    );
  }

  protected get firstName(): string {
    return this.userName.split(' ')[0];
  }
}

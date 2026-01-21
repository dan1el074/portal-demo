import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true,
  pure: false
})
export class TimeAgoPipe implements PipeTransform {

  transform(value: Date | string): string {
    if (!value) return '';

    const now = new Date().getTime();
    const date = new Date(value).getTime();
    const diff = Math.floor((now - date) / 1000);

    if (diff < 60) return 'agora mesmo';

    const minutes = Math.floor(diff / 60);
    if (minutes < 60) return `${minutes} minuto${minutes > 1 ? 's' : ''}`;

    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours} hora${hours > 1 ? 's' : ''}`;

    const days = Math.floor(hours / 24);
    if (days < 30) return `${days} dia${days > 1 ? 's' : ''}`;

    const months = Math.floor(days / 30);
    if (months < 12) return `${months} mês${months > 1 ? 'es' : ''}`;

    const years = Math.floor(months / 12);
    return `${years} ano${years > 1 ? 's' : ''}`;
  }
}

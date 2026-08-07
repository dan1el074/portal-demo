import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page500',
  imports: [RouterLink],
  templateUrl: './page500.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './page500.component.scss',
})
export class Page500Component {

}

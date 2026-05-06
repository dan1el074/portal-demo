import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardBodyComponent, CardComponent, CardLinkDirective, CardTitleDirective } from '@coreui/angular';
import { cilCopy } from '@coreui/icons';
import { CommonModule } from '@angular/common';
import { FileCard } from '../../../app/interface/file.interface';

@Component({
  selector: 'app-files',
  imports: [
    CommonModule,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    CardLinkDirective,
    RouterLink
  ],
  templateUrl: './files.component.html',
  styleUrl: './files.component.scss',
})
export class FilesComponent {
  @Input() files!: Array<FileCard>;
  icons = { cilCopy };
}

import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent, ButtonDirective, CardBodyComponent, CardComponent, CardImgDirective, CarouselConfig, ColComponent, ContainerComponent, DropdownComponent, DropdownItemDirective, DropdownMenuDirective, DropdownToggleDirective, ModalToggleDirective, RowComponent } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilOptions, cilPencil, cilTrash } from '@coreui/icons';
import { CarouselCustomConfig } from './carouse.config';
import { TimeAgoPipe } from './../../../app/pipes/time-ago.pipe';
import { ImageComponent } from '../../modal/media/image-modal/image.component';
import { PostCard } from '../../../app/interface/post.interface';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss',
  imports: [
    CommonModule,
    ContainerComponent,
    AvatarComponent,
    CardComponent,
    CardImgDirective,
    CardBodyComponent,
    RowComponent,
    ColComponent,
    ModalToggleDirective,
    ImageComponent,
    TimeAgoPipe,
    DropdownComponent,
    DropdownToggleDirective,
    DropdownMenuDirective,
    DropdownItemDirective,
    ButtonDirective,
    IconDirective
  ],
  providers: [{ provide: CarouselConfig, useClass: CarouselCustomConfig }],
})
export class PostComponent {
  @Input() post!: PostCard;
  @Input() canPost!: Boolean;
  @Output() editTask = new EventEmitter<number>();
  @Output() deleteTask = new EventEmitter<number>();

  protected slideIndex = 0;
  protected carouselReady = false;
  protected apiUrl = environment.apiUrl;
  protected icons = { cilOptions, cilPencil, cilTrash };

  constructor (private cdr: ChangeDetectorRef) {}

  protected openCarousel(index: number) {
    this.slideIndex = index;
    this.carouselReady = false;

    setTimeout(() => {
      this.carouselReady = true;
      this.cdr.detectChanges();
    });
  }

  protected onEdit(id: number): void {
    this.editTask.emit(id);
  }

  protected onDelete(id: number): void {
    this.deleteTask.emit(id);
  }
}

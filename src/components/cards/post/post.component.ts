import { CarouselCustomConfig } from './carouse.config';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AvatarComponent, ButtonCloseDirective, CardBodyComponent, CardComponent, CardImgDirective, CarouselConfig, ColComponent, ContainerComponent, ModalToggleDirective, RowComponent } from '@coreui/angular';
import { PostCard } from '../../../app/interface/post.interface';
import { ImageComponent } from '../../modal/image/image.component';

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
    ButtonCloseDirective,
    RowComponent,
    ColComponent,
    ModalToggleDirective,
    ImageComponent
  ],
  providers: [{ provide: CarouselConfig, useClass: CarouselCustomConfig }],
})
export class PostComponent {
  @Input() post!: PostCard;
  protected slideIndex = 0;
  protected carouselReady = false;

  openCarousel(index: number) {
    this.slideIndex = index;
    this.carouselReady = false;

    setTimeout(() => {
      this.carouselReady = true;
    });
  }
}

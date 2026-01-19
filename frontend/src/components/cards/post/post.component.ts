import { CarouselCustomConfig } from './carouse.config';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AvatarComponent, CardBodyComponent, CardComponent, CardImgDirective, CarouselConfig, ColComponent, ContainerComponent, ModalToggleDirective, RowComponent } from '@coreui/angular';
import { PostCard } from '../../../app/interface/post.interface';
import { ImageComponent } from '../../modal/image-modal/image.component';
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
    ImageComponent
  ],
  providers: [{ provide: CarouselConfig, useClass: CarouselCustomConfig }],
})
export class PostComponent {
  @Input() post!: PostCard;
  protected slideIndex = 0;
  protected carouselReady = false;
  protected apiUrl = environment.apiUrl;

  openCarousel(index: number) {
    this.slideIndex = index;
    this.carouselReady = false;

    setTimeout(() => {
      this.carouselReady = true;
    });
  }
}

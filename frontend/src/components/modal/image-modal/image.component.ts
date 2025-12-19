import { Component, Input, OnInit } from '@angular/core';
import { ButtonCloseDirective, CarouselComponent, CarouselControlComponent, CarouselIndicatorsComponent, CarouselInnerComponent, CarouselItemComponent, ModalBodyComponent, ModalComponent, ModalToggleDirective } from '@coreui/angular';
import { PostCard } from '../../../app/interface/post.interface';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-image',
  imports: [
    CommonModule,
    ModalComponent,
    ModalBodyComponent,
    ModalToggleDirective,
    ButtonCloseDirective,
    CarouselComponent,
    CarouselIndicatorsComponent,
    CarouselInnerComponent,
    CarouselItemComponent,
    CarouselControlComponent,
    RouterLink
  ],
  templateUrl: './image.component.html',
  styleUrl: './image.component.scss',
})
export class ImageComponent implements OnInit {
  @Input() post!: PostCard;
  @Input() index!: number;
  @Input() ready!: boolean;
  protected slides: any[] = [];

  ngOnInit(): void {
    for(let i=0; i<this.post.pictures.length; i++) {
      this.slides[i] = {id: this.post.pictures[i].id, src: this.post.pictures[i].uri};
    }
  }

}

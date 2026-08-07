import { Component, Input, OnChanges, OnInit, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { ButtonCloseDirective, CarouselComponent, CarouselControlComponent, CarouselIndicatorsComponent, CarouselInnerComponent, CarouselItemComponent, ModalBodyComponent, ModalComponent, ModalToggleDirective } from '@coreui/angular';
import { PostCard } from '../../../../app/interface/post.interface';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environment';

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
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './image.component.scss',
})
export class ImageComponent implements OnInit, OnChanges {
  @Input() post!: PostCard;
  @Input() index!: number;
  @Input() ready!: boolean;

  protected apiUrl = environment.apiUrl;
  protected slides: any[] = [];

  ngOnInit(): void {
    this.buildSlides();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post']) this.buildSlides();
  }

  protected selectSlide(index: number): void {
    this.index = index;
  }

  private buildSlides(): void {
    this.slides = [];
    for(let i=0; i<this.post.pictures.length; i++) {
      this.slides[i] = { id: this.post.pictures[i].id, src: this.apiUrl + '/images/' + this.post.pictures[i].id };
    }
  }
}

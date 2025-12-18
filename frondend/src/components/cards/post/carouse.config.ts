import { Injectable } from '@angular/core';

@Injectable()
export class CarouselCustomConfig {
  animate = false;
  direction: 'next' | 'prev' = 'next';
}

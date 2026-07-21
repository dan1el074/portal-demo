import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoModalComponent } from './video-modal.component';

describe('VideoModalComponent', () => {
  let component: VideoModalComponent;
  let fixture: ComponentFixture<VideoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VideoModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

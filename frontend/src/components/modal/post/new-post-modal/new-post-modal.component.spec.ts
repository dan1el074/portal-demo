import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewPostModalComponent } from './new-post-modal.component';

describe('NewPostModalComponent', () => {
  let component: NewPostModalComponent;
  let fixture: ComponentFixture<NewPostModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewPostModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewPostModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

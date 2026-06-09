import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PositionDeleteModalComponent } from './position-delete-modal.component';

describe('PositionDeleteModalComponent', () => {
  let component: PositionDeleteModalComponent;
  let fixture: ComponentFixture<PositionDeleteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PositionDeleteModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PositionDeleteModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

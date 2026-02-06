import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewCiModalComponent } from './new-ci-modal.component';

describe('NewCiModalComponent', () => {
  let component: NewCiModalComponent;
  let fixture: ComponentFixture<NewCiModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewCiModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewCiModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

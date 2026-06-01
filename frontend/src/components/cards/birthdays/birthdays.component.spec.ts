import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BirthdaysComponent } from './birthdays.component';

describe('BirthdaysComponent', () => {
  let component: BirthdaysComponent;
  let fixture: ComponentFixture<BirthdaysComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BirthdaysComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BirthdaysComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

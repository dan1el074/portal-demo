import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewspaperComponent } from './newspaper.component';

describe('NewspaperComponent', () => {
  let component: NewspaperComponent;
  let fixture: ComponentFixture<NewspaperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewspaperComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewspaperComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

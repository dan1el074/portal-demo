import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InternalControlComponent } from './internal-control.component';

describe('InternalControlComponent', () => {
  let component: InternalControlComponent;
  let fixture: ComponentFixture<InternalControlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InternalControlComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InternalControlComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

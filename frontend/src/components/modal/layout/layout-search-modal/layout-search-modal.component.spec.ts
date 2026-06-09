import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutSearchModalComponent } from './layout-search-modal.component';

describe('LayoutSearchModalComponent', () => {
  let component: LayoutSearchModalComponent;
  let fixture: ComponentFixture<LayoutSearchModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutSearchModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutSearchModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

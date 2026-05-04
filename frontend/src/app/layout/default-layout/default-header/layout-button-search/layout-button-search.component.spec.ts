import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutButtonSearchComponent } from './layout-button-search.component';

describe('LayoutButtonSearchComponent', () => {
  let component: LayoutButtonSearchComponent;
  let fixture: ComponentFixture<LayoutButtonSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutButtonSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutButtonSearchComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

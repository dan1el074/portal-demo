import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CiTableComponent } from './memorando-table.component';

describe('CiTableComponent', () => {
  let component: CiTableComponent;
  let fixture: ComponentFixture<CiTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CiTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CiTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

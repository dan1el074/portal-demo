import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepartmentTableComponent } from './department-table.component';

describe('DepartmentTableComponent', () => {
  let component: DepartmentTableComponent;
  let fixture: ComponentFixture<DepartmentTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartmentTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepartmentTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

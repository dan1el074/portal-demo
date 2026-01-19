import { AbstractControl, FormGroup, ValidationErrors } from "@angular/forms";

export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const form = control as FormGroup;
  const password = form.get('password')?.value;
  const repeat = form.get('repeatPassword')?.value;

  if (!password && !repeat) return null;

  return password === repeat ? null : { passwordMismatch: true };
}

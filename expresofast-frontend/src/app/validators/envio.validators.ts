import { AbstractControl, AsyncValidatorFn, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable, of, timer } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { EnvioService } from '../services/envio.service';

/**
 * Validador SÍNCRONO a nivel de FormGroup (cross-field):
 * fechaEntregaEstimada debe ser estrictamente mayor que fechaDespacho.
 */
export const fechasValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const despacho = group.get('fechaDespacho')?.value as string | null;
  const entrega = group.get('fechaEntregaEstimada')?.value as string | null;
  if (!despacho || !entrega) {
    return null; // los campos vacíos los cubre Validators.required
  }
  // Formato ISO yyyy-MM-dd: la comparación de fechas es matemática vía Date
  return new Date(entrega).getTime() > new Date(despacho).getTime()
    ? null
    : { fechasInvalidas: true };
};

/**
 * Validador ASÍNCRONO: consulta al API si el número de rastreo ya existe.
 * Retorna un Observable (la respuesta HTTP llega en una tarea futura del Event Loop).
 * El timer implementa debounce: si el usuario sigue escribiendo, switchMap
 * cancela la petición anterior (Angular desuscribe el Observable previo).
 */
export function trackingUnicoValidator(envioService: EnvioService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const valor = (control.value as string | null)?.trim();
    if (!valor) {
      return of(null);
    }
    return timer(400).pipe(
      switchMap(() => envioService.checkTracking(valor)),
      map(resp => (resp.existe ? { trackingTomado: true } : null)),
      catchError(() => of(null))
    );
  };
}
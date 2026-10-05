import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EnvioService } from '../../services/envio.service';
import { EnvioRegistroPayload } from '../../models/envio.model';
import { fechasValidator, trackingUnicoValidator } from '../../validators/envio.validators';

/** Formulario tipado de un paquete (pesoKg es number, nunca string). */
type PaqueteFormGroup = FormGroup<{
  descripcion: FormControl<string>;
  pesoKg: FormControl<number | null>;
}>;

@Component({
  selector: 'app-envio-avanzado-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './envio-avanzado-form.component.html',
  styleUrls: ['./envio-avanzado-form.component.css']
})
export class EnvioAvanzadoFormComponent {
  private fb = inject(NonNullableFormBuilder);
  private envioService = inject(EnvioService);
  private router = inject(Router);

  errorServidor = '';
  enviando = false;

  form = this.fb.group(
    {
      numeroTracking: this.fb.control('', {
        validators: [Validators.required, Validators.minLength(4)],
        asyncValidators: [trackingUnicoValidator(this.envioService)],
        updateOn: 'change'
      }),
      destinatario: this.fb.control('', Validators.required),
      direccionDestino: this.fb.control('', Validators.required),
      montoFlete: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
      fechaDespacho: this.fb.control('', Validators.required),
      fechaEntregaEstimada: this.fb.control('', Validators.required),
      paquetes: this.fb.array<PaqueteFormGroup>([this.crearPaquete()], Validators.minLength(1))
    },
    { validators: [fechasValidator] }
  );

  get paquetes(): FormArray<PaqueteFormGroup> {
    return this.form.controls.paquetes;
  }

  private crearPaquete(): PaqueteFormGroup {
    return this.fb.group({
      descripcion: this.fb.control('', Validators.required),
      pesoKg: this.fb.control<number | null>(null, [
        Validators.required,
        Validators.min(0.01),
        Validators.max(999.99)
      ])
    });
  }

  agregarPaquete(): void {
    this.paquetes.push(this.crearPaquete());
  }

  eliminarPaquete(index: number): void {
    if (this.paquetes.length > 1) {
      this.paquetes.removeAt(index);
    }
  }

  registrar(): void {
    if (this.form.invalid || this.form.pending) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const payload: EnvioRegistroPayload = {
      numeroTracking: v.numeroTracking.trim(),
      destinatario: v.destinatario,
      direccionDestino: v.direccionDestino,
      montoFlete: v.montoFlete as number,
      fechaDespacho: v.fechaDespacho,
      fechaEntregaEstimada: v.fechaEntregaEstimada,
      paquetes: v.paquetes.map(p => ({ descripcion: p.descripcion, pesoKg: p.pesoKg as number }))
    };
    this.enviando = true;
    this.errorServidor = '';
    this.envioService.crearEnvio(payload).subscribe({
      next: () => this.router.navigate(['/envios']),
      error: err => {
        this.enviando = false;
        this.errorServidor = err?.error?.message || 'Error al registrar el envío';
      }
    });
  }
}
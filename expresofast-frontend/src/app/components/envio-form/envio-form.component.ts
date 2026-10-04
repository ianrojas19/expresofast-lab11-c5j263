import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EnvioService } from '../../services/envio.service';
import { CrearEnvioPayload } from '../../models/envio.model';

@Component({
  selector: 'app-envio-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-form.component.html',
  styleUrls: ['./envio-form.component.css']
})
export class EnvioFormComponent {
  envioService = inject(EnvioService);
  router = inject(Router);

  payload: CrearEnvioPayload = {
    destinatario: '',
    direccionDestino: '',
    montoFlete: 0
  };

  registrar() {
    if (this.payload.destinatario && this.payload.direccionDestino && this.payload.montoFlete > 0) {
      this.envioService.crearEnvio(this.payload).subscribe({
        next: () => {
          alert('Envío registrado con éxito');
          this.router.navigate(['/envios']);
        },
        error: (err) => {
          alert('Error al registrar envío');
        }
      });
    } else {
      alert('Por favor, complete todos los campos correctamente');
    }
  }
}

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnvioService } from '../../services/envio.service';
import { Envio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-tracking.component.html',
  styleUrls: ['./envio-tracking.component.css']
})
export class EnvioTrackingComponent {
  codigoRastreo: string = '';
  envioService = inject(EnvioService);
  envio: Envio | null = null;
  errorMsg: string = '';

  buscar() {
    if (this.codigoRastreo) {
      this.envioService.obtenerPorRastreo(this.codigoRastreo).subscribe({
        next: (data) => {
          this.envio = data;
          this.errorMsg = '';
        },
        error: (err) => {
          this.envio = null;
          this.errorMsg = 'No se encontró ningún envío con ese código de rastreo.';
        }
      });
    }
  }

  getProgreso(estado: string): number {
    switch (estado) {
      case 'PENDIENTE': return 25;
      case 'EN_TRANSITO': return 50;
      case 'ENTREGADO': return 100;
      case 'CANCELADO': return 0;
      default: return 0;
    }
  }
}

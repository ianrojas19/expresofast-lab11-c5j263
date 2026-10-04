import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnvioService } from '../../services/envio.service';
import { Envio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './envio-list.component.html',
  styleUrls: ['./envio-list.component.css']
})
export class EnvioListComponent implements OnInit {
  envios: Envio[] = [];
  envioService = inject(EnvioService);

  ngOnInit() {
    this.cargarEnvios();
  }

  cargarEnvios() {
    this.envioService.obtenerEnvios().subscribe(data => {
      this.envios = data;
    });
  }

  actualizarEstado(id: number, event: any) {
    const nuevoEstado = event.target.value;
    this.envioService.actualizarEstado(id, nuevoEstado).subscribe(() => {
      this.cargarEnvios();
    });
  }

  getBadgeClass(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return 'badge pending';
      case 'EN_TRANSITO': return 'badge transit';
      case 'ENTREGADO': return 'badge delivered';
      case 'CANCELADO': return 'badge canceled';
      default: return 'badge';
    }
  }
}

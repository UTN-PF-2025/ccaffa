package ar.utn.ccaffa.model.dto;

import lombok.Data;

@Data
public class Bloque {
    Float x, y;
    Float ancho, largo;

    public Bloque(Float x, Float y, Float ancho, Float largo) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.largo = largo;
    }

    @Override
    public String toString() {
        return "Bloque{" + "x=" + x + ", y=" + y + ", " + ancho + "x" + largo + '}';
    }
}

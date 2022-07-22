package com.example.lexico.Model;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

import java.util.ArrayList;

public class Caso {

    private String nombre;
    private ArrayList<Participante> par;
    private Ellipse ellipse;

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void setEllipse(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

    public Caso(String nombre, ArrayList<Participante> par) {
        this.nombre = nombre;
        this.par = par;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Participante> getPar() {
        return par;
    }

    public void setPar(ArrayList<Participante> par) {
        this.par = par;
    }


}

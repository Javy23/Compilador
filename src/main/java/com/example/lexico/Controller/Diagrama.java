package com.example.lexico.Controller;

import com.example.lexico.Model.Caso;
import com.example.lexico.Model.Participante;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Diagrama implements Initializable {

    @FXML
    Pane fondo;
    @FXML
    Ellipse caso1;
    @FXML
    Ellipse caso2;
    @FXML
    Ellipse caso3;
    @FXML
    Ellipse caso4;
    @FXML
    Ellipse caso5;
    @FXML
    Ellipse caso6;
    @FXML
    Ellipse caso7;
    @FXML
    Ellipse caso8;
    @FXML
    Ellipse caso9;
    @FXML
    Label label1;
    @FXML
    Label label2;
    @FXML
    Label label3;
    @FXML
    Label label4;
    @FXML
    Label label5;
    @FXML
    Label label6;
    @FXML
    Label label7;
    @FXML
    Label label8;
    @FXML
    Label label9;
    @FXML
    ImageView p1;
    @FXML
    ImageView p2;
    @FXML
    ImageView p3;
    @FXML
    ImageView p4;
    @FXML
    ImageView p5;
    @FXML
    ImageView p6;
    @FXML
    ImageView p7;
    @FXML
    ImageView p8;
    @FXML
    ImageView p9;
    @FXML
    Label n1;
    @FXML
    Label n2;
    @FXML
    Label n3;
    @FXML
    Label n4;
    @FXML
    Label n5;
    @FXML
    Label n6;
    @FXML
    Label n7;
    @FXML
    Label n8;
    @FXML
    Label n9;
    @FXML
    Label titulo;

    ArrayList<Ellipse> casosF = new ArrayList<>();
    ArrayList<Label> casosT = new ArrayList<>();
    ArrayList<ImageView> monos = new ArrayList<>();
    ArrayList<Label> monosN = new ArrayList<>();

    ArrayList<Caso> casosCompletos = new ArrayList<>();
    HashMap<String, Participante> part = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

//       Casos
        this.casosF.add(this.caso1);
        this.casosF.add(this.caso2);
        this.casosF.add(this.caso3);
        this.casosF.add(this.caso4);
        this.casosF.add(this.caso5);
        this.casosF.add(this.caso6);
        this.casosF.add(this.caso7);
        this.casosF.add(this.caso8);
        this.casosF.add(this.caso9);

//       Casos nombre
        this.casosT.add(this.label1);
        this.casosT.add(this.label2);
        this.casosT.add(this.label3);
        this.casosT.add(this.label4);
        this.casosT.add(this.label5);
        this.casosT.add(this.label6);
        this.casosT.add(this.label7);
        this.casosT.add(this.label8);
        this.casosT.add(this.label9);

//      Monos
        this.monos.add(p1);
        this.monos.add(p2);
        this.monos.add(p3);
        this.monos.add(p4);
        this.monos.add(p5);
        this.monos.add(p6);
        this.monos.add(p7);
        this.monos.add(p8);
        this.monos.add(p9);

//       Monos nombre
        this.monosN.add(n1);
        this.monosN.add(n2);
        this.monosN.add(n3);
        this.monosN.add(n4);
        this.monosN.add(n5);
        this.monosN.add(n6);
        this.monosN.add(n7);
        this.monosN.add(n8);
        this.monosN.add(n9);


    }

    public void datos(ArrayList<Caso> casos, HashMap<String, Participante> pa, String titulo) {
        this.casosCompletos = casos;
        this.part = pa;

        int i = 0;
        String texto = "";

        this.titulo.setText(titulo);

        for (Caso c : casosCompletos) {
            this.casosF.get(i).setVisible(true);

            String ids = "";
            int k = 0;
            for (Participante p : c.getPar()) {
                ids += p.getId();

                if(k+1 != c.getPar().size())
                {
                    ids += ", ";
                }
                k++;
            }

            texto = "" + c.getNombre() + "\n ( " + ids + " )";
            this.casosT.get(i).setText(texto);
            i++;

            if(i == 9)
            {
                break;
            }
        }

        int j = 0;
        for (Map.Entry<String, Participante> entry : this.part.entrySet())
        {
          this.monos.get(j).setVisible(true);
          this.monosN.get(j).setText(entry.getValue().getId()+ ": "+entry.getValue().getNombre());
          j++;
        }



}}
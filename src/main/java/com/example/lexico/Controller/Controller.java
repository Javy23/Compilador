package com.example.lexico.Controller;

import com.example.lexico.Model.Caso;
import com.example.lexico.Model.Participante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Controller implements Initializable {

    @FXML
    Button btnVisualizar;
    @FXML
    TextArea procesoSemantico;
    @FXML
    Button btnAnalizar;
    @FXML
    TextArea entrada;
    @FXML
    TextArea procesoLexico;
    @FXML
    TextArea procesoSintactico;

    HashMap<String, String> tokens = new HashMap<>();
    HashMap<String, String[]> gramatica = new HashMap<>();
    String[][] tabla = new String[19][13];
    String[] terminales;
    Sheet sheet;
    ArrayList<Caso> casosCompletos = new ArrayList<>();
    HashMap<String,Participante> pa = new HashMap<>();
    String titulo = "";


    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        token();
        gramar();

    }

    @FXML
    public void analizar()
    {
        procesoLexico.clear();
        procesoSintactico.clear();
        procesoSemantico.clear();
        this.btnVisualizar.setDisable(true);
        this.pa.clear();
        this.casosCompletos.clear();
        boolean encontrado = false;
        String texto = entrada.getText();

        String[] newText = texto.split("\\s+");


        for (String s : newText) {
            encontrado = false;
            for (Map.Entry<String, String> entry : this.tokens.entrySet()) {
                Pattern patternName = Pattern.compile(entry.getValue());
                Matcher c = patternName.matcher(s);

                if (c.matches())
                {
                    encontrado = true;
                    this.procesoLexico.appendText(s +" es un --> "+ entry.getKey()+"\n\n");
                    break;
                }
            }

            if(!encontrado)
            {
                this.procesoLexico.appendText(s +" --> no pertenece a ningun token\n\n");

            }
        }

        leer();
        sintactico(newText, texto);



    }

    public void leer()
    {

        try {
            String f = "src/main/resources/Tabla sintactica.xlsx";
            FileInputStream inputStream = new FileInputStream(f);
            Workbook workbook = new XSSFWorkbook(inputStream);
            this.sheet = workbook.getSheetAt(0);

            Iterator<Row> filas = sheet.iterator();
            Iterator<Cell> celdas;

            int i = 0;
            Row fila;
            Cell celda;
            DataFormatter formatter = new DataFormatter();
            while(filas.hasNext()) {

                fila = filas.next();
                celdas = fila.cellIterator();
                int lastColumn = Math.max(fila.getLastCellNum(), 12 );

                for (int cn = 0; cn <lastColumn; cn++) {
                     celda = fila.getCell( cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL );
                    if(celda == null)
                    {
                        this.tabla[i][cn] = " ";
                    }
                    else
                    {
                        this.tabla[i][cn] = String.valueOf(celda);
                    }

                }

                i++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void sintactico(String[] texto, String t)
    {
        boolean error = false;
        Stack<String> pila = new Stack<>();
        pila.push("S");


        int index = 0;

        do{
            String cima = pila.peek();
            String cadena = texto[index];


            boolean si = this.gramatica.containsKey(cima);

            if(!si){
                Pattern patternName = Pattern.compile(cima);
                Matcher c = patternName.matcher(cadena);
                if(c.matches())
                {
                    pila.pop();
                    index++;
                    this.procesoSintactico.appendText(cadena + "  --> es un terminal\n\n");
                }
            }
            else
            {
                int fila = 0 ;
                int columna = 0;

                for(int i=0; i< tabla.length; i++)
                {
                    if(tabla[i][0].equals(cima))
                    {
                        fila = i;
                        i = tabla.length;
                    }

                }


                for(int j=0; j< tabla[0].length; j++)
                {

                    Pattern patternName = Pattern.compile(tabla[0][j]);
                    Matcher c = patternName.matcher(cadena);
                    if(c.matches())
                    {

                        columna = j;
                        j = tabla[0].length;
                    }

                }


                String ver = tabla[fila][columna];
                if((columna== 0))
                {

                    this.procesoSintactico.appendText(cadena + "  ---> ERROR SINTACTICO\n\n");
                    error = true;
                }
                else
                {
                    if(ver.equals(" "))
                    {
                        this.procesoSintactico.appendText(cadena + "  ---> ERROR SINTACTICO\n\n");
                        error = true;

                    }
                    else
                    {
                        String[] newText = ver.split("\\s+");
                        pila.pop();
                        for(int i=newText.length-1; i>=0; i--)
                        {
                            if(!newText[i].equals("ε"))
                            {
                                pila.push(newText[i]);
                            }
                        }
                    }
                }
            }

            if(!pila.isEmpty() && index == texto.length)
            {
                this.procesoSintactico.appendText(cadena + "  ---> ERROR SINTACTICO\n\n");
                error = true;
            }



        } while(!pila.isEmpty() && !error);

        if(!error)
        {
            this.procesoSintactico.appendText("Codigo sin errores sintacticos");
            this.semantica(t);
        }


    }
    public void semantica(String t)
    {
        ArrayList<String> id;
        ArrayList<String> name;
        ArrayList<String> caso;
        ArrayList<ArrayList<String>> casoPart = new ArrayList<>();
        boolean seguir, seguirN, seguirC = true;

        String[] tit = t.split("([:])");
        String[] titul = tit[1].split("([(])");
        this.titulo = titul[0];

        String[] separar = t.split("Participantes");
        separar = separar[1].split("Casos");
        String[] user = separar[0].split("\\s+");
        String[] casos = separar[1].split(tokens.get("IDENTIFICADOR"));

        id = this.dividir(tokens.get("DIGITO"), user);
        name = this.dividir(tokens.get("IDENTIFICADOR"), user);
        caso = this.dividir(tokens.get("IDENTIFICADOR"), separar[1].split("\\s+"));

        for(String s: casos) {

            casoPart.add(this.dividir(tokens.get("DIGITO"), s.split("\\s+")));
        }

        casoPart.remove(0);
        seguir = this.verR(id, "ID", "Paticipantes");
        seguirN = this.verR(name, "Nombre", "Paticipantes");
        seguirC = this.verR(caso, "Nombre", "Caso");

        boolean seguirId = true;

        for(ArrayList<String> c: casoPart)
        {
            boolean s1 = this.verR(c, "ID", "Casos");
            boolean s2 = this.verR(c,id);
            if(!s1 || !s2)
            {
                seguirId = false;
            }


        }



            if(seguir && seguirN && seguirC && seguirId)
            {

                this.procesoSemantico.appendText("Codigo sin errores semanticos");
                this.casosCompletos = this.datosF(id, name, caso, casoPart);
                this.btnVisualizar.setDisable(false);

            }





    }

    public  ArrayList<Caso> datosF(ArrayList<String> id, ArrayList<String> name, ArrayList<String> caso,  ArrayList<ArrayList<String>> casoPart)
    {
//        HashMap<String,Participante> pa = new HashMap<>();
        ArrayList<Caso> casCompletos = new ArrayList<>();

        for(int i = 0; i< id.size(); i++) {
            Participante p = new Participante(id.get(i), name.get(i));
            this.pa.put(id.get(i), p);
        }

        for(int i = 0; i< caso.size(); i++) {

            ArrayList<Participante> cas = new ArrayList<>();
            ArrayList<String> buscar = casoPart.get(i);

            for (String s : buscar) {
                Participante b = this.pa.get(s);
                cas.add(b);
            }
            Caso c = new Caso(caso.get(i), cas);
            c.setPar(cas);
            casCompletos.add(c);
        }

        return casCompletos;

    }

    public boolean verR(ArrayList<String> lista, String dato, String quien)
    {
        boolean seguir = true;
        ArrayList<String> num = new ArrayList<>(lista);

        HashSet<String> hs = new HashSet<>(num);
        num.clear();
        num.addAll(hs);

        for(String s: num){

            if(Collections.frequency(lista,s) > 1)
            {
                System.out.println();
                this.procesoSemantico.appendText(s+" ----> de "+quien+" ya ah sido definido\n\n");
                seguir = false;
            }

        }

        return seguir;
    }


    public boolean verR(ArrayList<String> lista, ArrayList<String> id)
    {
        boolean seguir = true;
        HashSet<String> hs = new HashSet<>(lista);
        HashSet<String> hs2 = new HashSet<>(id);
        lista.clear();
        lista.addAll(hs);
        id.clear();
        id.addAll(hs2);

        for(String s: lista){


            if(Collections.frequency(id,s) == 0)
            {
                System.out.println();
                this.procesoSemantico.appendText("ID ----> "+s+" : no ha sido declarado\n\n");
                seguir = false;
            }

        }
        return seguir;
    }

    public ArrayList<String> dividir(String rx , String[] sep )
    {
        ArrayList<String> lista = new ArrayList<>();

        for (String s : sep) {
            Pattern patternName = Pattern.compile(rx);
            Matcher c = patternName.matcher(s);
            if (c.matches()) {
                lista.add(s);
            }

        }
        return lista;
    }

    public void token()
    {
        tokens.put("RESERVADAS","(Titulo|Participantes|Casos)");
        tokens.put("IDENTIFICADOR","([a-z|A-Z])+");
        tokens.put("DIGITO","([1-9])");
        tokens.put("COMA","(,)");
        tokens.put("PARENTESIS ABIERTO","([(])");
        tokens.put("PARENTESIS CERRADO","([)])");
        tokens.put("DOS PUNTOS","([:])");
        tokens.put("SIMBOLO MAYOR","([>])");
        tokens.put("SIMBOLO MENOR","([<])");
    }

    public void gramar()
    {
        gramatica.put("S", terminales = new String[]{"TITULO 2P LETRA RL PA PART SME USUARIOS RU SMA CASOS SME CASO RC SMA PC"});
        gramatica.put("TITULO", terminales = new String[]{"Titulo"});
        gramatica.put("2P", terminales = new String[]{"([:])"});
        gramatica.put("LETRA",terminales = new String[]{"([a-z|A-Z])+"});
        gramatica.put("RL", terminales = new String[]{"LETRA RL","ε"});
        gramatica.put("PA", terminales = new String[]{"([(])"});
        gramatica.put("PART", terminales = new String[]{"Participantes"});
        gramatica.put("SME", terminales = new String[]{"([<])"});
        gramatica.put("USUARIOS", terminales = new String[]{"DIGITO 2P LETRA RL"});
        gramatica.put("RU", terminales = new String[]{"COMA USUARIOS RU", "ε"});
        gramatica.put("DIGITO",terminales = new String[]{"([1-9])"});
        gramatica.put("CASO", terminales = new String[]{"LETRA RL PA DIGITO RD PC"});
        gramatica.put("RD", terminales = new String[]{"COMA DIGITO RD","ε" });
        gramatica.put("RC", terminales = new String[]{"COMA CASO RC", "ε"});
        gramatica.put("COMA", terminales = new String[]{"(,)"});
        gramatica.put("PC", terminales = new String[]{"([)])"});
        gramatica.put("SMA", terminales = new String[]{"([>])"});
        gramatica.put("CASOS", terminales = new String[]{"Casos"});
    }

    @FXML
    public void visualizar(ActionEvent actionEvent)
    {

        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/example/lexico/diagrama.fxml"));
            Parent root = loader.load();
            Diagrama controlador = loader.getController();
            controlador.datos(this.casosCompletos, this.pa, this.titulo);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
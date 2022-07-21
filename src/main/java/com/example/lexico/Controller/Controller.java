package com.example.lexico;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Controller implements Initializable {


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
        sintactico(newText);



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

    public void sintactico(String[] texto)
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
        }
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


}